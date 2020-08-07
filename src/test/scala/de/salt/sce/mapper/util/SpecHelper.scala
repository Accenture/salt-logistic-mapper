package de.salt.sce.mapper.util

import akka.actor.ActorSystem
import de.salt.sce.mapper.server.ActorService

object SpecHelper {
  def beforeAll(system: ActorSystem): Unit = {
    ActorService.setActorSystem(system)
    ActorService.createActorHierarchy()
  }
}