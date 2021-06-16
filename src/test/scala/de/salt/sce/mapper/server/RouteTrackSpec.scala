package de.salt.sce.mapper.server

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.server.communication.server.AkkaHttpRestServer
import de.salt.sce.mapper.server.util.LazyConfig
import de.salt.sce.mapper.util.{ObjectSerializer, SpecHelper}
import de.salt.sce.model.csv.PaketCSV
import de.salt.sce.model.edifact.Transport
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import java.io.File
import scala.concurrent.duration.DurationInt

class RouteTrackSpec extends WordSpec with Matchers
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

  private val appHomePath = new File(".").getCanonicalPath

  override def beforeAll(): Unit = {
    SpecHelper.beforeAll(system)
    route = AkkaHttpRestServer.getServer.getRoute
  }

  "AkkaHttpRestServer" should {
/*
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

        responseProtocol.edifactResponse.get.success.size should be(1)
        responseProtocol.csvResponse should be(Option.empty)

        val line1: Option[String] = responseProtocol.edifactResponse.get.success.get(file)
        val transport = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[Transport];

        transport.getShipments.get(0).getPakets.get(0).getCni.getDocumentMessageNumber should be("279289")
        transport.getShipments.get(0).getPakets.get(0).getSts.getEvent should be("50")
        transport.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("18899031")
        transport.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("201703290851")

        val line2: Option[String] = responseProtocol.edifactResponse.get.error.get("Unknown")
        line2.get should be("File Parsing Exception:Failed to filter source. - Unknown")
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

        responseProtocol.csvResponse.get.success.size should be(2)
        responseProtocol.edifactResponse should be(Option.empty)

        val line1: Option[String] = responseProtocol.csvResponse.get.success.get(file)
        val paketCSVs1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[PaketCSV]]

        paketCSVs1.size() should be(85)

        paketCSVs1.get(0).getSdgdatum should be("20160130023900")
        paketCSVs1.get(0).getLangreferenz should be("09445744184617")
        paketCSVs1.get(0).getStatus should be("18")

        paketCSVs1.get(84).getSdgdatum should be("20160130054905")
        paketCSVs1.get(84).getLangreferenz should be("09445744184916")
        paketCSVs1.get(84).getStatus should be("02")

        val line2: Option[String] = responseProtocol.csvResponse.get.success.get("Unknown")
        val paketCSVs2 = ObjectSerializer.deserialize(Base64.decodeBase64(line2.get)).asInstanceOf[java.util.ArrayList[PaketCSV]]

        paketCSVs2.size() should be(0)
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

        responseProtocol.edifactResponse.get.success.size should be(1)
        responseProtocol.edifactResponse.get.error.size should be(1)
        responseProtocol.csvResponse should be(Option.empty)

        val line1: Option[String] = responseProtocol.edifactResponse.get.success.get(file)
        val transport = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[Transport];

        transport.getShipments.size() should be(1);
        transport.getShipments.get(0).getPakets.size() should be(9)

        transport.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("01092352456")
        transport.getShipments.get(0).getPakets.get(0).getRffs.get(0).getQualifier should be("FF")
        transport.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("201301250900")
        transport.getShipments.get(0).getPakets.get(0).getNads.get(0).getName1 should be("DACHSER, STAVENHAGEN")
        transport.getShipments.get(0).getPakets.get(0).getNads.get(0).getQualifier should be("CS")

        val line2: Option[String] = responseProtocol.edifactResponse.get.error.get("Unknown")
        line2.get should be("File Parsing Exception:Failed to filter source. - Unknown")
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

        responseProtocol.csvResponse.get.success.size should be(2)
        responseProtocol.edifactResponse should be(Option.empty)

        val line1: Option[String] = responseProtocol.csvResponse.get.success.get(file)
        val paketCSVs1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[PaketCSV]]

        paketCSVs1.size() should be(52)
        paketCSVs1.get(0).getSdgdatum should be("SDGDATUM")
        paketCSVs1.get(0).getLangreferenz should be("LANGREFERENZ        ")
        paketCSVs1.get(0).getTofsdgnr should be("TOFSDGNR")
        paketCSVs1.get(0).getEmpfaenger should be("NAME EMPFF?NGER/ABHOLADR.     ")
        paketCSVs1.get(0).getLkz should be("LKZ")
        paketCSVs1.get(0).getPlz should be("PLZ    ")
        paketCSVs1.get(0).getOrt should be("ORT                           ")
        paketCSVs1.get(0).getDienst2 should be("DIENST2                                      ")

        paketCSVs1.get(51).getSdgdatum should be("20140630")
        paketCSVs1.get(51).getLangreferenz should be("0080913281          ")
        paketCSVs1.get(51).getTofsdgnr should be("44594647")
        paketCSVs1.get(51).getEmpfaenger should be("BAUHAUS DEPOT GMBH            ")
        paketCSVs1.get(51).getLkz should be("AT ")
        paketCSVs1.get(51).getPlz should be("4400   ")
        paketCSVs1.get(51).getOrt should be("STEYR                         ")
        paketCSVs1.get(51).getDienst2 should be("Export CPT (Frei Haus)                       ")

        val line2: Option[String] = responseProtocol.csvResponse.get.success.get("Unknown")
        val paketCSVs2 = ObjectSerializer.deserialize(Base64.decodeBase64(line2.get)).asInstanceOf[java.util.ArrayList[PaketCSV]]

        paketCSVs2.size() should be(0)
      }
    }

    s"testing is ups service returns correct response when becomes correct request [$path]" in {
      import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed

      val file1: String = "20170516_093419_20160719_141122_ROTH-IFTSTA"
      val file2: String = "20160123_181643_ROTH-IFTSTA.399"

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "ups",
        configFile = "config-ups.xml",
        messageType = "edifact",
        encoding = "windows-1252",
        files = Map(
          file1 -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975+UN+D:07B'UNH+00000000806777+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'NAD+DP++++21 HOERNLEWEG:E+WEILHEIM++73235+DE'RFF+CW:1Z3F4W576807747148'CNI+1'LOC+14+:::WENDLINGEN+:::DE'STS+1:D2:21'RFF+AGY:WALCH'RFF+AEL:RESIDENTIAL'RFF+AAN:2'DTM+78:20160714173746:204'UNT+14+00000000806777'UNH+00000000806778+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+562V50'NAD+BS+562V50:01'RFF+CW:1Z562V506807737844'CNI+1'LOC+14+:::BRUSSELS+:::BE'STS+1:E1:101'DTM+78:20160714173339:204'FTX+AVA+02++DELIVERY WILL BE RESCHEDULED.:RESOLUTION'FTX+AVA+MF++THIS PACKAGE IS BEING HELD FOR A FUTURE DELIVERY DATE.:REASON'UNT+12+00000000806778'UNE+2+00000000044975'UNZ+1+00000000044975'",
          file2 -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384+UN+D:07B'UNH+00000000638669+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071118'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638669'UNH+00000000638670+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071136'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638670'UNE+2+00000000034384'UNZ+1+00000000034384'",
          "Unknown" -> "Unknown format"
        )
      )

      Post(path, mapperRequest) ~>
        addCredentials(validCredentials) ~> route ~> check {
        logger.debug(s"Response status: $status")
        status shouldEqual StatusCodes.OK
        val responseProtocol = entityAs[InternalResponse]
        logger.debug(s"ResponseProtocol: $responseProtocol")

        responseProtocol.edifactResponse.get.success.size should be(2)
        responseProtocol.edifactResponse.get.error.size should be(1)
        responseProtocol.csvResponse should be(Option.empty)

        val line1: Option[String] = responseProtocol.edifactResponse.get.success.get(file1)
        val transport1 = ObjectSerializer.deserialize(Base64.decodeBase64(line1.get)).asInstanceOf[Transport]

        transport1.getShipments.size() should be(1);
        transport1.getShipments.get(0).getPakets.size() should be(2)
        transport1.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("1Z3F4W576807747148")
        transport1.getShipments.get(0).getPakets.get(0).getRffs.get(1).getReference should be("WALCH")
        transport1.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("20160714173746")
        transport1.getShipments.get(0).getPakets.get(0).getNads.size() should be(3)
        transport1.getShipments.get(0).getPakets.get(0).getNads.get(2).getStreet1 should be("21 HOERNLEWEG")

        val line2: Option[String] = responseProtocol.edifactResponse.get.success.get(file2)
        val transport2 = ObjectSerializer.deserialize(Base64.decodeBase64(line2.get)).asInstanceOf[Transport]

        transport2.getShipments.size() should be(1);
        transport2.getShipments.get(0).getPakets.size() should be(2)
        transport2.getShipments.get(0).getPakets.get(0).getSts.getEvent should be("101")
        transport2.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("1Z3F4W576807071118")
        transport2.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("20160123023727")
        transport2.getShipments.get(0).getPakets.get(0).getNads.size() should be(2)

        val line3: Option[String] = responseProtocol.edifactResponse.get.error.get("Unknown")
        line3.get should be("File Parsing Exception:Failed to filter source. - Unknown")
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

 */
  }

  private def createPost(usingPath: String, request: IncompatibleProviderRequest): HttpRequest = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    Post(usingPath, request)
  }

  case class IncompatibleProviderRequest(configName: Int)

}
