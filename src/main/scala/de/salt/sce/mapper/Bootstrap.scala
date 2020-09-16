package de.salt.sce.mapper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.ActorService
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object Bootstrap extends App with LazyLogging with LazyConfig {

  protected var bindingFuture: Future[Http.ServerBinding] = _

  config.checkValid(config, "sce.track.mapper.rest-server")

  protected val protocol = config.getString("sce.track.mapper.rest-server.protocol")
  protected val endpoint = config.getString("sce.track.mapper.rest-server.endpoint")
  protected val endpointPort = config.getInt("sce.track.mapper.rest-server.port")

  implicit val system: ActorSystem = ActorSystem(config.getString("sce.track.mapper.actor-system.name"))
  ActorService.setActorSystem(system)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  logger.debug("Creating manager actors")
  ActorService.createActorHierarchy()

  val restServer = AkkaHttpRestServer.getServer
  bindingFuture = Http().bindAndHandle(restServer.getRoute, endpoint, endpointPort)
  logPID()
  logger.info(s"SALT Software mapper Track Server online at: $protocol://$endpoint:$endpointPort/")

  protected def logPID(): Unit = {
    try {
      java.lang.management.ManagementFactory.getRuntimeMXBean.getName.split('@').headOption.foreach { pid =>
        logger.info(s"Running PID: [$pid]")
      }
    } catch {
      case e: Exception =>
        logger.warn(s"Found exception while retrieving PID: $e")
    }
  }

  // once ready to terminate the server, invoke terminate:
  def onceAllConnectionsTerminated: Future[Http.HttpTerminated] = {
    logger.info("Shutting down server")
    Await.result(bindingFuture, 10.seconds)
      .terminate(hardDeadline = 3.seconds)
  }
}