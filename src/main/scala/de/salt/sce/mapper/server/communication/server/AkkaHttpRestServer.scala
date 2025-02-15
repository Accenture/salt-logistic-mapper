package de.salt.sce.mapper.server.communication.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import de.salt.sce.mapper.BuildInfo
import de.salt.sce.mapper.server.ActorService
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Formats, Serialization, native}

import java.util.concurrent.TimeUnit.SECONDS
import scala.concurrent.duration.Duration

/**
 * Companion Object
 */
object AkkaHttpRestServer extends LazyLogging {
  private val restServer = new AkkaHttpRestServer
  def getServer: AkkaHttpRestServer = restServer
}

/**
 * Akka Http Server
 */
class AkkaHttpRestServer extends RestServer with LazyLogging with LazyConfig {

  private val mapperPath = config.getString("sce.track.mapper.rest-server.path.mapper-path")
  private val mapperExt = config.getString("sce.track.mapper.rest-server.path.mapper-ext")

  implicit val timeout: Timeout = Timeout(Duration(config.getInt("sce.track.mapper.rest-server.timeout-sec"), SECONDS))
  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats

  /**
   * Get all defined routes
   *
   * @return Defined route
   */
  def getRoute: Route = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

    // Rejection handler
    val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

    // Exception handler
    val exceptionHandler = ExceptionHandler { case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
    }

    // Combining the two handlers only for convenience
    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

    handleErrors {
      cors() {
        handleErrors {
          path("") { // default - GET on root
            get {
              val buildInfo =
                s"""Service is up and running.
                   | Provider Name: ${BuildInfo.name}
                   | Provider Version: ${BuildInfo.version}""".stripMargin
              logger.info(buildInfo)
              complete(StatusCodes.OK, buildInfo.replaceAll("\n","").replaceAll("\r",""))
            }
          } ~ path(mapperPath / mapperExt) {
            withoutSizeLimit {
            post {
              authenticateBasic(realm = "sce", sceAuthenticator) { _ =>
                handleMappingRequest()
              }
            }
            }
          }
        }
      }
    }
  }

  protected def sceAuthenticator(credentials: Credentials): Option[Credentials] =
    credentials match {
      case p@Credentials.Provided(id) if id == config.getString(s"sce.track.mapper.rest-server.auth.username")
        && p.verify(config.getString(s"sce.track.mapper.rest-server.auth.password"))
      => Some(p)
      case _ => None
    }

  protected def handleMappingRequest(): Route = {
    entity(as[MapperRequest]) {
      request =>
        onSuccess(ActorService.getMapperClientManagerActor ? request) {
          case response: InternalResponse =>
            logger.debug(s"Response: $response")
            complete(StatusCodes.getForKey(response.statusCode).getOrElse(InternalServerError), response )

          case x: Any =>
            logger.error(s"Internal error: Got unexpected response ${x.toString}")
            complete(InternalServerError, x.toString)
        }
    }
  }
}
