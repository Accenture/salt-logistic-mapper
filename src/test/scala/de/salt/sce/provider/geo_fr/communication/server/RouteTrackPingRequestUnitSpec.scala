package de.salt.sce.provider.geo_fr.communication.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import de.salt.sce.provider.model.communication.model.Requests.TrackPingRequest
import de.salt.sce.provider.model.communication.model.Responses.TrackResponseProtocol
import de.salt.sce.provider.model.communication.model.secret.LoggableSecretSerializer
import de.salt.sce.provider.model.communication.model.{RequestData, Responses}
import de.salt.sce.provider.model.communication.server.AkkaHttpRestServer
import de.salt.sce.provider.model.util.LazyConfig
import de.salt.sce.provider.model.{ActorService, Provider}
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class RouteTrackPingRequestUnitSpec extends WordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {
  private var route: Route = _

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats + new LoggableSecretSerializer

  Provider.setProviderName("geo_fr")
  private val trackPath = config.getString(s"sce.track.${Provider.getName}.rest-server.path.track-path")
  private val pingWithTrackRequestExt = config.getString(s"sce.track.${Provider.getName}.rest-server.path.ping-request")
  private val validCredentials = BasicHttpCredentials(
    config.getString(s"sce.track.${Provider.getName}.rest-server.auth.username"),
    config.getString(s"sce.track.${Provider.getName}.rest-server.auth.password"))

  override def beforeAll(): Unit = {
    ActorService.setActorSystem(system)
    ActorService.createActorHierarchy()
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    s"return a protocol echo response for authenticated POST requests to [/$trackPath/$pingWithTrackRequestExt]" in {

      val pingTrackRequest = TrackPingRequest(
        request = List(buildRequestData())
      )

      Post(s"/$trackPath/$pingWithTrackRequestExt", pingTrackRequest) ~> addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[TrackResponseProtocol]
        logger.debug(s"ResponseProtocol: $responseProtocol")
        responseProtocol.successProtocol should not be empty
        responseProtocol.successProtocol.head.status should be(Responses.Echo)
        responseProtocol.successProtocol.head.hash should be(pingTrackRequest.request.head.hash)
      }
    }

    s"return a protocol echo response for authenticated POST requests (with microserviceName) to [/$trackPath/$pingWithTrackRequestExt] (support deprecated case)" in {

      val pingTrackRequest = TrackPingRequest(
        request = List(buildRequestData())
      )

      Post(s"/$trackPath/$pingWithTrackRequestExt", pingTrackRequest) ~> addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[TrackResponseProtocol]
        logger.debug(s"ResponseProtocol: $responseProtocol")
        responseProtocol.successProtocol should not be empty
        responseProtocol.successProtocol.head.status should be(Responses.Echo)
        responseProtocol.successProtocol.head.hash should be(pingTrackRequest.request.head.hash)
      }
    }

    s"return a protocol error response for incomplete authenticated POST requests to [/$trackPath/$pingWithTrackRequestExt]" in {

      val pingTrackRequest = TrackPingRequest(
        request = Nil
      )

      Post(s"/$trackPath/$pingWithTrackRequestExt", pingTrackRequest) ~> addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.BadRequest
        val responseProtocol = entityAs[TrackResponseProtocol]
        logger.debug(s"ResponseProtocol: $responseProtocol")
        responseProtocol.errorProtocol should not be empty
      }
    }
  }

  private def buildRequestData(): RequestData = {
    RequestData(
      hash = Random.nextString(10),
      host = "localhost",
      port = 22,
      username = "Micky",
      password = "Mouse",
      remotePath = "/test",
      fileCompressed = "false",
      remoteFilename = "abcd",
      encoding = "UTF-8",
      payload = "xabcd",
      sshKey = "",
      sshKeyPass = ""
    )
  }
}
