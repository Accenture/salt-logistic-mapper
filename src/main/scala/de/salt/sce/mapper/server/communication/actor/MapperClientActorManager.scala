package de.salt.sce.mapper.server.communication.actor

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

/**
 * Mapper-Client Manager Companion Object
 */
object MapperClientActorManager {
  final val Name: String = "mapper-client-manager"

  def props: Props = Props(new MapperClientActorManager)
}


/**
 * Mapper-Client Manager:
 * Parent of Mapper Client
 */
class MapperClientActorManager extends Actor with LazyLogging {
  override def preStart(): Unit = {
    logger.debug("Creating Mapper Client")
    context.actorOf(MapperClientActor.props, MapperClientActor.Name)
  }

  override def postStop(): Unit =
    logger.debug("Mapper Client Manager stopped")

  def receive: Receive = Actor.emptyBehavior
}