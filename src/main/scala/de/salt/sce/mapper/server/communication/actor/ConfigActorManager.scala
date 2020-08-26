package de.salt.sce.mapper.server.communication.actor

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

/**
 * Track-Client Manager Companion Object
 */
object ConfigActorManager {
  final val Name: String = "track-client-manager"

  def props: Props = Props(new ConfigActorManager)
}


/**
 * Track-Client Manager:
 * Parent of Track Client
 */
class ConfigActorManager extends Actor with LazyLogging {
  override def preStart(): Unit = {
    logger.debug("Creating Track Client")
    context.actorOf(ConfigActor.props, ConfigActor.Name)
  }

  override def postStop(): Unit =
    logger.debug("Track Client Manager stopped")

  def receive: Receive = Actor.emptyBehavior
}
