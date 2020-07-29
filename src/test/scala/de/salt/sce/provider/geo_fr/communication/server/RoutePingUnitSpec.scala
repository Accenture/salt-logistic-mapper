package de.salt.sce.provider.geo_fr.communication.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.provider.model.communication.model.Requests.PingRequest
import de.salt.sce.provider.model.communication.model.Responses
import de.salt.sce.provider.model.communication.model.Responses.TrackResponseProtocol
import de.salt.sce.provider.model.communication.model.secret.LoggableSecretSerializer
import de.salt.sce.provider.model.communication.server.AkkaHttpRestServer
import de.salt.sce.provider.model.util.LazyConfig
import de.salt.sce.provider.model.{ActorService, Provider}
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class RoutePingUnitSpec extends WordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {
  private var route: Route = _

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats + new LoggableSecretSerializer

  Provider.setProviderName("geo_fr")
  private val trackPath = config.getString(s"sce.track.${Provider.getName}.rest-server.path.track-path")
  private val ping = config.getString(s"sce.track.${Provider.getName}.rest-server.path.ping")

  override def beforeAll(): Unit = {
    ActorService.setActorSystem(system)
    ActorService.createActorHierarchy()
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    "return OK for GET requests to the root path" in {
      Get() ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    s"return a string echo response for GET requests to [/$ping]" in {
      Get(s"/$ping") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] should not be empty
      }
    }

    s"reject requests to non-existing route [/$trackPath/$ping-denpfadgibtsnet]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

      val pingRequest = PingRequest(hash = Random.nextString(10))
      val wrongPath = s"/$trackPath/$ping-denpfadgibtsnet"
      Post(wrongPath, pingRequest) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    s"return an error for NON-authenticated POST requests to [/$trackPath/$ping]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
      val pingRequest = PingRequest(hash = Random.nextString(10))
      Post(s"/$trackPath/$ping", pingRequest) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    s"return an error for invalid credentials on POST requests to [/$trackPath/$ping]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
      val pingRequest = PingRequest(hash = Random.nextString(10))
      val invalidCredentials = BasicHttpCredentials("Micky", "Maus")
      Post(s"/$trackPath/$ping", pingRequest) ~> addCredentials(invalidCredentials) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    s"return a correct protocol echo response for authenticated POST requests to [/$trackPath/$ping]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
      val pingRequest = PingRequest(hash = Random.nextString(10))
      val validCredentials = BasicHttpCredentials(
        config.getString(s"sce.track.${Provider.getName}.rest-server.auth.username"),
        config.getString(s"sce.track.${Provider.getName}.rest-server.auth.password"))
      Post(s"/$trackPath/$ping", pingRequest) ~> addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[TrackResponseProtocol]
        responseProtocol.successProtocol should not be empty
        responseProtocol.successProtocol.head.hash should be(pingRequest.hash)
        responseProtocol.successProtocol.head.status should be(Responses.Echo)
      }
    }

    "leave GET requests to other path [/invalidPath] unhandled" in {
      Get("/invalidPath") ~> route ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET, POST"
      }
    }
  }
}
