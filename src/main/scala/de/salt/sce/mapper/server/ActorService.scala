package de.salt.sce.mapper.server

import java.util.concurrent.TimeUnit.MILLISECONDS

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import akka.util.Timeout
import de.salt.sce.mapper.server.communication.actor.{MapperClientActor, MapperClientActorManager}
import de.salt.sce.mapper.server.communication.server.{MapperServer, MapperServerManager}
import de.salt.sce.mapper.server.util.LazyConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Actor Service
 */
object ActorService extends LazyConfig {
  private val defaultDuration = Duration(500, MILLISECONDS)
  var system: ActorSystem = _
  private var mapperServerActor: Option[ActorRef] = None
  private var mapperClientActor: Option[ActorRef] = None
  implicit private val timeout: Timeout = Timeout(defaultDuration)

  def createActorHierarchy(): Unit = {
    mapperServerActor = None
    mapperClientActor = None
    getActorSystem match {
      case Some(s) =>
        s.actorOf(MapperServerManager.props, MapperServerManager.Name)
        s.actorOf(MapperClientActorManager.props, MapperClientActorManager.Name)
      case None =>
        throw new Exception("Actor system is not available")
    }
  }

  def getMapperServerActor: ActorRef = {
    mapperServerActor match {
      case Some(a) => a
      case None =>
        mapperServerActor = Some(Await.result(
          getActor(s"/user/${MapperServerManager.Name}/${MapperServer.Name}")
            .resolveOne(), defaultDuration))
        mapperServerActor.get
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

  def getMapperClientActor: ActorRef = {
    mapperClientActor match {
      case Some(a) => a
      case None =>
        mapperClientActor =
          Some(Await.result(
            getActor(s"/user/${MapperClientActorManager.Name}/${MapperClientActor.Name}")
              .resolveOne(), defaultDuration))
        mapperClientActor.get
    }
  }
}
