package de.salt.sce.mapper.server.communication.server

import akka.http.scaladsl.server.Route

/**
 * REST Server Trait
 */
trait RestServer {
  def getRoute: Route
}
