package de.salt.sce.mapper.server

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.model.TrackContract
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig
import de.salt.sce.mapper.util.{ObjectSerializer, SpecHelper}
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
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
  implicit val formats: Formats = DefaultFormats

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(200.seconds)

  private val path = s"/${config.getString("sce.track.mapper.rest-server.path.mapper-path")}/${config.getString("sce.track.mapper.rest-server.path.mapper-ext")}"
  private var route: Route = _

  override def beforeAll(): Unit = {
    SpecHelper.beforeAll(system)
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {

    s"testing is amm service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val file: String = "P0815-STAT_IFTSTA-4.txt"
      val content: String = IOUtils.toString(this.getClass.getResourceAsStream(s"/smooks/amm/$file"), UTF_8)

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "amm",
        configFile = "config-amm.xml",
        messageType = "edifact",
        encoding = "UTF-8",
        files = Map(
          file -> content,
          "Unknown" -> "Unknown format"
        )
      )
      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.extResponse.success.size equals 1
        responseProtocol.extResponse.error.size equals 1

        val line1: Option[String] = responseProtocol.extResponse.success.get(file)
        val trackContractList1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[TrackContract]];

        trackContractList1.size() equals 1
        trackContractList1.get(0).getRefId equals "279289"
        trackContractList1.get(0).getRefType equals "XSISHP"
        trackContractList1.get(0).getStateId equals "50"
        trackContractList1.get(0).getTimestamp equals "20170329085100"
        trackContractList1.get(0).getEdcid equals "amm"

        val line2: Option[String] = responseProtocol.extResponse.error.get("Unknown")
        line2.get equals "Some(File Parsing Exception:Failed to filter source. - Unknown)"
      }
    }

    s"testing is dpd service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val file: String = "20160201_170112_STATUSDATA_KD2748208P_D20160201T021335"
      val content: String = IOUtils.toString(this.getClass.getResourceAsStream(s"/smooks/dpd/$file"), UTF_8)

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "dpd",
        configFile = "config-dpd.xml",
        messageType = "csv",
        encoding = "UTF-8",
        files = Map(
          file -> content,
          "Unknown" -> "Unknown format"
        )
      )
      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.extResponse.success.size equals 1
        responseProtocol.extResponse.error.size equals 1

        val line1: Option[String] = responseProtocol.extResponse.success.get(file)
        val trackContractList1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[TrackContract]];

        trackContractList1.size() equals 85
        trackContractList1.get(0).getRefId equals "09445744184617"
        trackContractList1.get(0).getRefType equals "XSITRA"
        trackContractList1.get(0).getStateId equals "18"
        trackContractList1.get(0).getStateTextExt equals "Zusatzinformation"
        trackContractList1.get(0).getTimestamp equals "20160130023900"
        trackContractList1.get(0).getEdcid equals "dpd"


        val line2: Option[String] = responseProtocol.extResponse.error.get("Unknown")
        line2.get equals "Some(File Parsing Exception:File format is wrong. - Unknown)"
      }
    }

    s"testing is dachser service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val file: String = "Beispiel_EDIFACT_IFTSTA_D01C.txt"
      val content: String = IOUtils.toString(this.getClass.getResourceAsStream(s"/smooks/dachser/$file"), UTF_8)

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "dachser",
        configFile = "config-dachser.xml",
        messageType = "edifact",
        encoding = "UTF-8",
        files = Map(
          file -> content,
          "Unknown" -> "Unknown format"
        )
      )
      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.extResponse.success.size equals 1
        responseProtocol.extResponse.error.size equals 1

        val line1: Option[String] = responseProtocol.extResponse.success.get(file)
        val trackContractList1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[TrackContract]];

        trackContractList1.size() equals 9
        trackContractList1.get(0).getRefId equals "2006830538"
        trackContractList1.get(0).getRefType equals "XSITRA"
        trackContractList1.get(0).getStateId equals "45"
        trackContractList1.get(0).getTimestamp equals "timestamp"
        trackContractList1.get(0).getEdcid equals "dachser"

        val line2: Option[String] = responseProtocol.extResponse.error.get("Unknown")
        line2.get equals "Some(File Parsing Exception:Failed to filter source. - Unknown)"
      }
    }

    s"testing is tof (trans-o-flex) service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val file: String = "93449323.ROTHENBE.20140703.RZMX.39499687.06313600.CSV"
      val content: String = IOUtils.toString(this.getClass.getResourceAsStream(s"/smooks/tof/$file"), UTF_8)

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "tof",
        configFile = "config-tof.xml",
        messageType = "csv",
        encoding = "windows-1252",
        files = Map(
          file -> content,
          "Unknown" -> "Unknown format"
        )
      )
      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.extResponse.success.size equals 1
        responseProtocol.extResponse.error.size equals 1

        val line1: Option[String] = responseProtocol.extResponse.success.get(file)
        val trackContractList1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[TrackContract]];

        trackContractList1.size() equals 51
        trackContractList1.get(0).getRefId equals "0080893320"
        trackContractList1.get(0).getRefType equals "DELI"
        trackContractList1.get(0).getStateId equals "A"
        trackContractList1.get(0).getStateTextExt equals "Export CPT (Frei Haus)"
        trackContractList1.get(0).getTimestamp equals "20140602000000"
        trackContractList1.get(0).getEdcid equals "tof"
        trackContractList1.get(0).getName1 equals "HELLWEG - IHR BAUFREUND"
        trackContractList1.get(0).getPcode1 equals "73235"
        trackContractList1.get(0).getCity equals "WEILHEIM"
        trackContractList1.get(0).getCountryiso equals "AT"

        val line2: Option[String] = responseProtocol.extResponse.error.get("Unknown")
        line2.get equals "Some(File Parsing Exception:File format is wrong. - Unknown)"
      }
    }

    s"testing is ups service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "ups",
        configFile = "config-ups.xml",
        messageType = "edifact",
        encoding = "windows-1252",
        files = Map(
          "20170516_093419_20160719_141122_ROTH-IFTSTA" -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975+UN+D:07B'UNH+00000000806777+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'NAD+DP++++21 HOERNLEWEG:E+WEILHEIM++73235+DE'RFF+CW:1Z3F4W576807747148'CNI+1'LOC+14+:::WENDLINGEN+:::DE'STS+1:D2:21'RFF+AGY:WALCH'RFF+AEL:RESIDENTIAL'RFF+AAN:2'DTM+78:20160714173746:204'UNT+14+00000000806777'UNH+00000000806778+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+562V50'NAD+BS+562V50:01'RFF+CW:1Z562V506807737844'CNI+1'LOC+14+:::BRUSSELS+:::BE'STS+1:E1:101'DTM+78:20160714173339:204'FTX+AVA+02++DELIVERY WILL BE RESCHEDULED.:RESOLUTION'FTX+AVA+MF++THIS PACKAGE IS BEING HELD FOR A FUTURE DELIVERY DATE.:REASON'UNT+12+00000000806778'UNE+2+00000000044975'UNZ+1+00000000044975'",
          "20160123_181643_ROTH-IFTSTA.399" -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384+UN+D:07B'UNH+00000000638669+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071118'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638669'UNH+00000000638670+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071136'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638670'UNE+2+00000000034384'UNZ+1+00000000034384'",
          "Unknown" -> "Unknown format"
        )
      )

      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.extResponse.success.size equals 2
        responseProtocol.extResponse.error.size equals 1

        val line1: Option[String] = responseProtocol.extResponse.success.get("20170516_093419_20160719_141122_ROTH-IFTSTA")
        val trackContractList1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[TrackContract]];
        trackContractList1.size() equals 2
        trackContractList1.get(0).getRefId equals "1Z3F4W576807747148"
        trackContractList1.get(0).getRefType equals "XSITRA"
        trackContractList1.get(0).getStateId equals "21"
        trackContractList1.get(0).getTimestamp equals "20160714173746"
        trackContractList1.get(0).getEdcid equals "ups"
        trackContractList1.get(0).getStreet equals "21 HOERNLEWEG; E"
        trackContractList1.get(0).getPcode1 equals "73235"
        trackContractList1.get(0).getCity equals "WEILHEIM"
        trackContractList1.get(0).getCountryiso equals "DE"

        trackContractList1.get(1).getRefId equals "1Z562V506807737844"
        trackContractList1.get(1).getRefType equals "XSITRA"
        trackContractList1.get(1).getStateId equals "101"
        trackContractList1.get(1).getTimestamp equals "20160714173339"
        trackContractList1.get(1).getEdcid equals "ups"


        val line2: Option[String] = responseProtocol.extResponse.success.get("20160123_181643_ROTH-IFTSTA.399")
        val trackContractList2 = ObjectSerializer.deserialize(Base64.decodeBase64(line2.get)).asInstanceOf[java.util.ArrayList[TrackContract]];
        trackContractList2.size() equals 2
        trackContractList2.get(0).getRefId equals "1Z3F4W576807071118"
        trackContractList2.get(0).getRefType equals "XSITRA"
        trackContractList2.get(0).getStateId equals "101"
        trackContractList2.get(0).getTimestamp equals "20160123023727"
        trackContractList2.get(0).getEdcid equals "ups"

        trackContractList2.get(1).getRefId equals "1Z3F4W576807071136"
        trackContractList2.get(1).getRefType equals "XSITRA"
        trackContractList2.get(1).getStateId equals "101"
        trackContractList2.get(1).getTimestamp equals "20160123023727"
        trackContractList2.get(1).getEdcid equals "ups"

        val line3: Option[String] = responseProtocol.extResponse.error.get("Unknown")
        line3.get equals "File Parsing Exception:Failed to filter source. - Unknown"
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
          responseString should startWith("The request content was malformed:")
        }
    }
  }

  private def createPost(usingPath: String, request: IncompatibleProviderRequest): HttpRequest = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    Post(usingPath, request)
  }

  case class IncompatibleProviderRequest(configName: Int)

}
