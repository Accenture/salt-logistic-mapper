package de.salt.sce.mapper.server

import java.util.concurrent.TimeUnit.MILLISECONDS

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import akka.util.Timeout
import de.salt.sce.mapper.server.communication.client.{TrackClient, TrackClientManager}
import de.salt.sce.mapper.server.communication.server.{TrackServer, TrackServerManager}
import de.salt.sce.mapper.server.util.LazyConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Actor Service
 */
object ActorService extends LazyConfig {
  private val defaultDuration = Duration(500, MILLISECONDS)
  var system: ActorSystem = _
  private var trackServerActor: Option[ActorRef] = None
  private var trackClientActor: Option[ActorRef] = None
  implicit private val timeout: Timeout = Timeout(defaultDuration)

  def createActorHierarchy(): Unit = {
    trackServerActor = None
    trackClientActor = None
    getActorSystem match {
      case Some(s) =>
        s.actorOf(TrackServerManager.props, TrackServerManager.Name)
        s.actorOf(TrackClientManager.props, TrackClientManager.Name)
      case None =>
        throw new Exception("Actor system is not available")
    }
  }

  def getTrackServerActor: ActorRef = {
    trackServerActor match {
      case Some(a) => a
      case None =>
        trackServerActor = Some(Await.result(
          getActor(s"/user/${TrackServerManager.Name}/${TrackServer.Name}")
            .resolveOne(), defaultDuration))
        trackServerActor.get
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

  def getTrackClientActor: ActorRef = {
    trackClientActor match {
      case Some(a) => a
      case None =>
        trackClientActor =
          Some(Await.result(
            getActor(s"/user/${TrackClientManager.Name}/${TrackClient.Name}")
              .resolveOne(), defaultDuration))
        trackClientActor.get
    }
  }
}
