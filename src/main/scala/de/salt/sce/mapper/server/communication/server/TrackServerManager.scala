package de.salt.sce.mapper.server.communication.server

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.ActorMaterializer.create
import com.typesafe.config.ConfigObject
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.Bootstrap.config
import de.salt.sce.mapper.communication.getconfigs.ConfigCopy
import de.salt.sce.mapper.server.util.LazyConfig
import akka.http.javadsl.Http.get
import de.salt.sce.mapper.actor.ConfigActor;
/**
 * Track-Server Manager Companion Object
 */
object TrackServerManager {
  final val Name: String = "track-server-manager"

  def props: Props = Props(new TrackServerManager)
}

/**
 * Track-Server Manager:
 * Parent of Track Server
 */
class TrackServerManager extends Actor with LazyLogging with LazyConfig  {
  private lazy val configActor = loadActor("configActor", Props[ConfigActor])

  override def preStart(): Unit = {
    logger.debug("Getting Smooks Configurations.")
    configActor ! ""

    logger.debug("Creating Track Server")
    context.actorOf(TrackServer.props, TrackServer.Name)
  }

  override def postStop(): Unit =
    logger.debug("Track Server Manager stopped")

  def receive: Receive = Actor.emptyBehavior

  def loadActor(actorName: String, actorProps: Props): ActorRef =
    context.child(s"$actorName-${self.path.name}") match {
      case None =>
        logger.debug(s"creating $actorName: $actorName-${self.path.name}")
        context.actorOf(actorProps, s"$actorName-${self.path.name}")
      case Some(x) =>
        logger.debug(s"getting an existing $actorName actor")
        x
    }
}
