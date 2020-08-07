package de.salt.sce.mapper.server

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol
import de.salt.sce.mapper.server.communication.model.secret.LoggableSecretSerializer
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig
import de.salt.sce.mapper.util.SpecHelper
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.DurationInt

class RouteTrackIntegrationSpec extends WordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {

  private val validCredentials = BasicHttpCredentials(
    config.getString("sce.track.mapper.rest-server.auth.username"),
    config.getString("sce.track.mapper.rest-server.auth.password")
  )

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats + new LoggableSecretSerializer

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(20.seconds)

  private val path = s"/${config.getString("sce.track.mapper.rest-server.path.track-path")}/${config.getString("sce.track.mapper.rest-server.path.track-ext")}"
  private var route: Route = _

  override def beforeAll(): Unit = {
    SpecHelper.beforeAll(system)
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    s"testing is service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val trackRequest = TrackProviderRequest(
        id = UUID.randomUUID().toString,
        configName = "ups",
        lines = Map("key1" -> "value1", "key2" -> "value2")
      )

      Post(path, trackRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[TrackResponseProtocol]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.success.isEmpty should be(false)
        responseProtocol.success.size should be(1)
        responseProtocol.error.size should be(1)
      }
    }

    "return an error on incompatible request structure" in {
      val incompatibleProviderRequest = IncompatibleProviderRequest(configName = 2)

      createPost(path, incompatibleProviderRequest) ~>
        addCredentials(validCredentials) ~>
        Route.seal(route) ~>
        check {
          logger.debug(s"Response status: $status")
          logger.debug(s"Response entity [$responseEntity]")
          logger.debug(s"Response [$response]")

          status shouldEqual StatusCodes.BadRequest
          val responseString = entityAs[String]
          logger.debug(s"Response text: $responseString")
          responseString should not be empty
          responseString should startWith ("The request content was malformed:")
        }
    }
  }

  private def createPost(usingPath: String, request: IncompatibleProviderRequest) : HttpRequest = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    Post(usingPath, request)
  }

  case class IncompatibleProviderRequest(configName: Int )
}
