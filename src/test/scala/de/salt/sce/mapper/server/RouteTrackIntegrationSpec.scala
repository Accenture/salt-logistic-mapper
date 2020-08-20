package de.salt.sce.mapper.server

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.MapperServiceClientTrack
import de.salt.sce.mapper.model.TrackContract
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest
import de.salt.sce.mapper.server.communication.model.Responses.InternalResponse
import de.salt.sce.mapper.server.communication.model.secret.LoggableSecretSerializer
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig
import de.salt.sce.mapper.util.SpecHelper
import org.apache.commons.codec.binary.Base64
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.DurationInt

class RouteTrackIntegrationSpec extends WordSpec with Matchers
  with ScalatestRouteTest with LazyConfig
  with LazyLogging {


  private val validCredentials = BasicHttpCredentials(
    config.getString("sce.track.mapper.rest-server.auth.username"),
    config.getString("sce.track.mapper.rest-server.auth.password")
  )

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats + new LoggableSecretSerializer

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(200.seconds)

  private val path = s"/${config.getString("sce.track.mapper.rest-server.path.track-path")}/${config.getString("sce.track.mapper.rest-server.path.track-ext")}"
  private var route: Route = _

  override def beforeAll(): Unit = {
    SpecHelper.beforeAll(system)
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    s"testing is service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val trackRequest = TrackProviderRequest(
        id = UUID.randomUUID().toString,
        configName = "ups",
        configFile = "config-ups.xml",
        messageType = "edifact",
        encoding = "windows-1252",
        lines = Map("20170516_093419_20160719_141122_ROTH-IFTSTA" -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975+UN+D:07B'UNH+00000000806777+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'NAD+DP++++21 HOERNLEWEG:E+WEILHEIM++73235+DE'RFF+CW:1Z3F4W576807747148'CNI+1'LOC+14+:::WENDLINGEN+:::DE'STS+1:D2:21'RFF+AGY:WALCH'RFF+AEL:RESIDENTIAL'RFF+AAN:2'DTM+78:20160714173746:204'UNT+14+00000000806777'UNH+00000000806778+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+562V50'NAD+BS+562V50:01'RFF+CW:1Z562V506807737844'CNI+1'LOC+14+:::BRUSSELS+:::BE'STS+1:E1:101'DTM+78:20160714173339:204'FTX+AVA+02++DELIVERY WILL BE RESCHEDULED.:RESOLUTION'FTX+AVA+MF++THIS PACKAGE IS BEING HELD FOR A FUTURE DELIVERY DATE.:REASON'UNT+12+00000000806778'UNE+2+00000000044975'UNZ+1+00000000044975'")
      )

      Post(path, trackRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        //val responseProtocol = entityAs[TrackResponseProtocol]
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")
        val line:Tuple2[String, String] = responseProtocol.extResponse.success.head

        val trackContractList = MapperServiceClientTrack.deserialize(Base64.decodeBase64(line._2)).asInstanceOf[java.util.ArrayList[TrackContract]];
        logger.debug(s"ResponseProtocol: $responseProtocol")


        trackContractList.get(0).getRefId equals "1Z3F4W576807747148"

        trackContractList.get(0).getRefId equals "1Z3F4W576807747148"
        trackContractList.get(0).getRefType equals "XSITRA"
        trackContractList.get(0).getStateId equals "21"
        trackContractList.get(0).getTimestamp equals "20160714173746"
        trackContractList.get(0).getEdcid equals "ups"
        trackContractList.get(0).getStreet equals "21 HOERNLEWEG; E"
        trackContractList.get(0).getPcode1 equals "73235"
        trackContractList.get(0).getCity equals "WEILHEIM"
        trackContractList.get(0).getCountryiso equals "DE"

        trackContractList.get(0).getRefId equals "1Z562V506807737844"
        trackContractList.get(0).getRefType equals "XSITRA"
        trackContractList.get(0).getStateId equals "101"
        trackContractList.get(0).getTimestamp equals "20160714173339"
        trackContractList.get(0).getEdcid equals "ups"
      }
    }

    "return an error on incompatible request structure" in {
      val incompatibleProviderRequest = IncompatibleProviderRequest(configName = 2)

      createPost(path, incompatibleProviderRequest) ~>
        addCredentials(validCredentials) ~>
        Route.seal(route) ~>
        check {
          logger.debug(s"Response status: $status")
          logger.debug(s"Response entity [$responseEntity]")
          logger.debug(s"Response [$response]")

          status shouldEqual StatusCodes.BadRequest
          val responseString = entityAs[String]
          logger.debug(s"Response text: $responseString")
          responseString should not be empty
          responseString should startWith ("The request content was malformed:")
        }
    }
  }

  private def createPost(usingPath: String, request: IncompatibleProviderRequest) : HttpRequest = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    Post(usingPath, request)
  }

  case class IncompatibleProviderRequest(configName: Int )
}
