package de.salt.sce.mapper.server.communication.actor

import akka.actor.{Actor, Props, Terminated}
import com.typesafe.scalalogging.LazyLogging
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import de.salt.sce.mapper.server.communication.model.MapperRequest

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
  val parallelFactor = 4

  var router:Router = {
    val routees = Vector.fill(parallelFactor) {
      val r = context.actorOf(Props[MapperClientActor])
      context.watch(r)
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive: Receive = {
    case mr: MapperRequest =>
      router.route(mr, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[MapperClientActor])
      context.watch(r)
      router = router.addRoutee(r)
  }
}