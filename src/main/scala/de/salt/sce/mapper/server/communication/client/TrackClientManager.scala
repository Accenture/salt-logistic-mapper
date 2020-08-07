package de.salt.sce.mapper.server.communication.client

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

/**
 * Track-Client Manager Companion Object
 */
object TrackClientManager {
  final val Name: String = "track-client-manager"

  def props: Props = Props(new TrackClientManager)
}


/**
 * Track-Client Manager:
 * Parent of Track Client
 */
class TrackClientManager extends Actor with LazyLogging {
  override def preStart(): Unit = {
    logger.debug("Creating Track Client")
    context.actorOf(TrackClient.props, TrackClient.Name)
  }

  override def postStop(): Unit =
    logger.debug("Track Client Manager stopped")

  def receive: Receive = Actor.emptyBehavior
}
