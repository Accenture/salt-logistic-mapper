package de.salt.sce.mapper.server.communication.server

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.actor.ConfigActor
import de.salt.sce.mapper.server.util.LazyConfig;

/**
 * Mapper-Server Manager Companion Object
 */
object MapperServerManager {
  final val Name: String = "mapper-server-manager"

  def props: Props = Props(new MapperServerManager)
}

/**
 * Mapper-Server Manager:
 * Parent of Mapper Server
 */
class MapperServerManager extends Actor with LazyLogging with LazyConfig {
  private lazy val configActor = loadActor("configActor", Props[ConfigActor])

  override def preStart(): Unit = {
    logger.debug("Getting Smooks Configurations.")
    configActor ! "INIT_CONFIG"

    logger.debug("Creating Mapper Server")
    context.actorOf(MapperServer.props, MapperServer.Name)
  }

  override def postStop(): Unit =
    logger.debug("Mapper Server Manager stopped")

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
