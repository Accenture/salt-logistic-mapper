package de.salt.sce.mapper.server

import java.util.concurrent.TimeUnit.MILLISECONDS

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import akka.util.Timeout
import de.salt.sce.mapper.server.communication.actor.{MapperActor, MapperActorDispatcher}
import de.salt.sce.mapper.server.util.LazyConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Actor Service
 */
object ActorService extends LazyConfig {
  private val defaultDuration = Duration(500, MILLISECONDS)
  var system: ActorSystem = _
  private var mapperClientActorManager: Option[ActorRef] = None
  implicit private val timeout: Timeout = Timeout(defaultDuration)

  def createActorHierarchy(): Unit = {
    mapperClientActorManager = None
    getActorSystem match {
      case Some(s) =>
        s.actorOf(MapperActorDispatcher.props, MapperActorDispatcher.Name)
      case None =>
        throw new Exception("Actor system is not available")
    }
  }

  /**
   * selects an Actor by path
   */
  def getActor(absolutePath: String): ActorSelection = {
    getActorSystem match {
      case Some(s) =>
        s.actorSelection(absolutePath)
      case None =>
        throw new Exception("Actor system is not available")
    }
  }

  def getActorSystem: Option[ActorSystem] = Option(system)

  def setActorSystem(system: ActorSystem): Unit = this.system = system

  def getMapperClientManagerActor: ActorRef = {
    mapperClientActorManager match {
      case Some(a) => a
      case None =>
        mapperClientActorManager =
          Some(Await.result(
            getActor(s"/user/${MapperActorDispatcher.Name}")
              .resolveOne(), defaultDuration))
        mapperClientActorManager.get
    }
  }
}
