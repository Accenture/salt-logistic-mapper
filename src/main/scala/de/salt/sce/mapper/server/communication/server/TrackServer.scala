package de.salt.sce.mapper.server.communication.server

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.ActorService
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest
import de.salt.sce.mapper.server.communication.model.Responses.{InternalResponse, TrackResponseProtocol}
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Serialization, native}

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object TrackServer {
  final val Name: String = "track-server"

  def props: Props = Props(new TrackServer)
}

class TrackServer extends Actor with LazyLogging with LazyConfig {

  implicit val s: Serialization = native.Serialization
  implicit val f: DefaultFormats = DefaultFormats
  implicit val timeout: Timeout = Timeout(Duration(config.getInt(s"sce.track.mapper.rest-server.timeout-sec"), SECONDS))
  implicit val ec: ExecutionContext = context.dispatcher

  def receive: Receive = {
    // productive case
    case trackRequest: TrackProviderRequest =>
      val senderRef = sender()
      logger.debug(s"Received request $trackRequest")

      ask(ActorService.getTrackClientActor, trackRequest)
        .mapTo[InternalResponse] pipeTo senderRef

    // catchall: unknown case
    case req =>
      val msg = s"Received unexpected request $req"
      logger.error(msg)
      sender() ! InternalResponse(
        id = "UNKNOWN",
        TrackResponseProtocol(
          success = mutable.HashMap(),
          error = mutable.HashMap()
        ),statusCode = StatusCodes.InternalServerError.intValue
      )
  }
}
