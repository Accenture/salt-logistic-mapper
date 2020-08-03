package de.salt.sce.provider.mapper.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import de.salt.sce.provider.mapper.util.SpecHelper
import de.salt.sce.provider.model.communication.model.RequestData
import de.salt.sce.provider.model.communication.model.Requests.TrackPingRequest
import de.salt.sce.provider.model.communication.model.Responses.TrackResponseProtocol
import de.salt.sce.provider.model.communication.model.secret.LoggableSecretSerializer
import de.salt.sce.provider.model.communication.model.{KeyValue, RequestData, TrackContract}
import de.salt.sce.provider.model.communication.server.AkkaHttpRestServer
import de.salt.sce.provider.model.util.LazyConfig
import org.json4s.native.Serialization.read
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.DurationInt

class RouteTrackIntegrationSpec extends WordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {
  private var route: Route = _

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats + new LoggableSecretSerializer

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(20.seconds)

  private val validCredentials = BasicHttpCredentials(
    config.getString("sce.track.mapper.rest-server.auth.username"),
    config.getString("sce.track.mapper.rest-server.auth.password")
  )

  private val path = s"/${config.getString("sce.track.mapper.rest-server.path.track-path")}/${config.getString("sce.track.mapper.rest-server.path.track-ext")}"


  override def beforeAll(): Unit = {
    SpecHelper.beforeAll(system)
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    s"testing is service online [$path]" in {

      val pingTrackRequest = TrackPingRequest(
        request = List(buildRealRequestDataZipped())
      )

      Post(path, pingTrackRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[TrackResponseProtocol]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.successProtocol.isEmpty should be(true)
      }
    }
  }

  private def buildRealRequestDataZipped(): RequestData = {
    RequestData(
      hash = "RBR",
      host = "testurl",
      port = 8082,
      remotePath = "",
      fileCompressed = "X",
      remoteFilename = "",
      encoding = "",
      payload = "504B0304140000000800277E4D5019E3DA3C0C0000000A000000070000006D795F646174613335343331B1303734330700504B01021300140000000800277E4D5019E3DA3C0C0000000A0000000700000000000000000000000000000000006D795F64617461504B0506000000000100010035000000310000000000"
    )
  }
}
