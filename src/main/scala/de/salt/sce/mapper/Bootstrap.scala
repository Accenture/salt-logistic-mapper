package de.salt.sce.mapper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


object Bootstrap extends App with LazyLogging with LazyConfig {

  protected var bindingFuture: Future[Http.ServerBinding] = _

  config.checkValid(config, s"sce.track.mapper.rest-server")

  protected val protocol = config.getString(s"sce.track.mapper.rest-server.protocol")
  protected val endpoint = config.getString(s"sce.track.mapper.rest-server.endpoint")
  protected val endpointPort = config.getInt(s"sce.track.mapper.rest-server.port")

  implicit val system: ActorSystem = ActorSystem(config.getString(s"sce.track.mapper.actor-system.name"))

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  bindingFuture = Http().bindAndHandle(
    new AkkaHttpRestServer(system).getRoute,
    endpoint,
    endpointPort
  )

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