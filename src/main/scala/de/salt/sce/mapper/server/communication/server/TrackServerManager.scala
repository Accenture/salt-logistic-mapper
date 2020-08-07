package de.salt.sce.mapper.server.communication.server

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

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
class TrackServerManager extends Actor with LazyLogging {
  override def preStart(): Unit = {
    logger.debug("Creating Track Server")
    context.actorOf(TrackServer.props, TrackServer.Name)
  }

  override def postStop(): Unit =
    logger.debug("Track Server Manager stopped")

  def receive: Receive = Actor.emptyBehavior
}
