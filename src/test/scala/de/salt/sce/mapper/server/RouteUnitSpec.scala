package de.salt.sce.mapper.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RouteUnitSpec extends AnyWordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats
  private var route: Route = _

  override def beforeAll(): Unit = {
    ActorService.setActorSystem(system)
    ActorService.createActorHierarchy()
    route = AkkaHttpRestServer.getServer.getRoute
  }

  private val path = s"/${config.getString(s"sce.track.mapper.rest-server.path.mapper-path")}/${config.getString(s"sce.track.mapper.rest-server.path.mapper-ext")}"

  "AkkaHttpRestServer" should {

    "return OK for GET requests to the root path" in {
      Get() ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "return MethodNotAllowed for POST requests to the root path" in {
      Post() ~> route ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
      }
    }

    "return MethodNotAllowed for non POST requests" in {
      Get(path) ~> route ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
      }
    }

    "handle GET requests to other path [/invalidPath]" in {
      Post("/invalidPath") ~> route ~> check {
        handled shouldBe true
      }
    }

    s"return an error for NON-authenticated POST requests to [$path]" in {
      Post(path) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    s"return an error for invalid credentials on POST requests to [$path]" in {
      val invalidCredentials = BasicHttpCredentials("Micky", "Maus")
      Post(path) ~> addCredentials(invalidCredentials) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }
  }
}
