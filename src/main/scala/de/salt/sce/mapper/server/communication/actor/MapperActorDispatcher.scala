package de.salt.sce.mapper.server.communication.actor

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.actor.ConfigActor
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.util.LazyConfig

/**
 * Mapper Actor Dispatcher Companion Object
 */
object MapperActorDispatcher {
  final val Name: String = "mapper-dispatcher"

  def props: Props = Props(new MapperActorDispatcher)
}

/**
 * Mapper Actor Dispatcher:
 * Parent of Mapper Actor
 */
class MapperActorDispatcher extends Actor with LazyLogging with LazyConfig {
  private val parallelFactor = config.getInt("sce.track.mapper.actor-system.parallelFactor")

  var router:Router = {
    val routees = Vector.fill(parallelFactor) {
      val r = context.actorOf(Props[MapperActor])
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
      val r = context.actorOf(Props[MapperActor])
      context.watch(r)
      router = router.addRoutee(r)
  }
}