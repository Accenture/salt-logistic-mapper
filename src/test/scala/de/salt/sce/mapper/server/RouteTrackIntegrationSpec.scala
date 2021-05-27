package de.salt.sce.mapper.server

import java.util.UUID
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.util.IntegrationTester
import de.salt.sce.mapper.util.ObjectSerializer.deserialize
import de.salt.sce.model.csv.PaketCSV
import de.salt.sce.model.edifact.{Paket, Shipment, Transport}
import org.apache.commons.codec.binary.Base64.decodeBase64

class RouteTrackIntegrationSpec extends IntegrationTester {

  private val mapperUri: String  = buildMapperUrl()

  override def beforeAll(): Unit = {
    logger.warn("Make sure Mapper is up and running!")
  }

  "Mapper" should {
    s"return a transports response for authenticated POST requests [$mapperUri] for UPS" in {
      val file1: String = "20170516_093419_20160719_141122_ROTH-IFTSTA"
      val file2: String = "20160123_181643_ROTH-IFTSTA.399"
      val file3: String = "Unknown"

      val mapperRequest = MapperRequest(
        id = UUID.randomUUID().toString,
        serviceName = "ups_de",
        configFile = "config-ups.xml",
        messageType = "edifact",
        encoding = "windows-1252",
        files = Map(
          file1 -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160714:1243+00000000044975+UN+D:07B'UNH+00000000806777+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'NAD+DP++++21 HOERNLEWEG:E+WEILHEIM++73235+DE'RFF+CW:1Z3F4W576807747148'CNI+1'LOC+14+:::WENDLINGEN+:::DE'STS+1:D2:21'RFF+AGY:WALCH'RFF+AEL:RESIDENTIAL'RFF+AAN:2'DTM+78:20160714173746:204'UNT+14+00000000806777'UNH+00000000806778+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+562V50'NAD+BS+562V50:01'RFF+CW:1Z562V506807737844'CNI+1'LOC+14+:::BRUSSELS+:::BE'STS+1:E1:101'DTM+78:20160714173339:204'FTX+AVA+02++DELIVERY WILL BE RESCHEDULED.:RESOLUTION'FTX+AVA+MF++THIS PACKAGE IS BEING HELD FOR A FUTURE DELIVERY DATE.:REASON'UNT+12+00000000806778'UNE+2+00000000044975'UNZ+1+00000000044975'",
          file2 -> "UNB+UNOA:1+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384++IFTSTA'UNG+IFTSTA+EURPROD:UPS+ROTH-DE-IFTSTA:02+160122:2143+00000000034384+UN+D:07B'UNH+00000000638669+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071118'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638669'UNH+00000000638670+IFTSTA:D:07B:UN'BGM+23:UPS::QVD+O4'NAD+DEQ+3F4W57'NAD+BS+3F4W57:01'RFF+CW:1Z3F4W576807071136'CNI+1'LOC+14+:::BASEL+:::CH'STS+1:E1:101'DTM+78:20160123023727:204'FTX+AVA+SR++YOUR PACKAGE IS AT THE CLEARING AGENCY AWAITING FINAL RELEASE.:REASON'UNT+11+00000000638670'UNE+2+00000000034384'UNZ+1+00000000034384'",
          file3 -> "Unknown format"
        )
      )

      val internalResponse:InternalResponse = createAndCheckAuthRequest(mapperRequest, mapperUri)

      internalResponse.edifactResponse.get.success.size should be(2)
      internalResponse.edifactResponse.get.error.size should be(1)
      internalResponse.csvResponse should be(Option.empty)

      val line1: Option[String] = internalResponse.edifactResponse.get.success.get(file1)
      val transport1 = deserialize(decodeBase64(line1.get)).asInstanceOf[Transport]

      transport1.getShipments.size() should be(1)
      transport1.getShipments.get(0).getPakets.size() should be(2)
      transport1.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("1Z3F4W576807747148")
      transport1.getShipments.get(0).getPakets.get(0).getRffs.get(1).getReference should be("WALCH")
      transport1.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("20160714173746")
      transport1.getShipments.get(0).getPakets.get(0).getNads.size() should be(3)
      transport1.getShipments.get(0).getPakets.get(0).getNads.get(2).getStreet1 should be("21 HOERNLEWEG")

      val line2: Option[String] = internalResponse.edifactResponse.get.success.get(file2)
      val transport2 = deserialize(decodeBase64(line2.get)).asInstanceOf[Transport]

      transport2.getShipments.size() should be(1)
      transport2.getShipments.get(0).getPakets.size() should be(2)
      transport2.getShipments.get(0).getPakets.get(0).getSts.getEvent should be("101")
      transport2.getShipments.get(0).getPakets.get(0).getRffs.get(0).getReference should be("1Z3F4W576807071118")
      transport2.getShipments.get(0).getPakets.get(0).getDtms.get(0).getDateTimePeriod should be("20160123023727")
      transport2.getShipments.get(0).getPakets.get(0).getNads.size() should be(2)

      val line3: Option[String] = internalResponse.edifactResponse.get.error.get(file3)
      line3 should be ('defined)
      line3.get should be(s"File Parsing Exception:Failed to filter source. - $file3")
    }

//    s"return a transports response for authenticated POST requests [$mapperUri] for GLS" in {
//      val file1: String = "20200923_133501_pakstat.018"
//
//      val mapperRequest = MapperRequest(
//        id = UUID.randomUUID().toString,
//        serviceName = "gls_de",
//        configFile = "config-gls.xml",
//        messageType = "csv",
//        encoding = "windows-1252",
//        files = Map(
//          file1 -> "85590441212|20201102|1200|2010|20201103|1057|4499|700002105|00340061160033296936|001324094|Amazon Brieselang GmbH|\n85756150125|20201102|2800|2011|20201103|1056|Hemdt|700002106|62597232|0001067650|Mercedes-Benz AG|\n85756150126|20201102|2800|2011|20201103|1056|Hemdt|700002106|62597224|0001067650|Mercedes-Benz AG|",
//        )
//      )
//
//      val internalResponse:InternalResponse = createAndCheckAuthRequest(mapperRequest, mapperUri)
//
//      internalResponse.csvResponse.get.success.size should be(1)
//      internalResponse.edifactResponse should be(Option.empty)
//
//      val line1: Option[String] = internalResponse.csvResponse.get.success.get(file1)
//      val packages = deserialize(decodeBase64(line1.get)).asInstanceOf[java.util.ArrayList[PaketCSV]]
//
//      packages.size should be(3)
//      packages.get(0).getLangreferenz should be("85590441212")
//      packages.get(0).getEmpfaenger should be("Amazon Brieselang GmbH")
//      packages.get(0).getSdgdatum should be("20201103")
//      packages.get(0).getSdgzeit should be("1057")
//      packages.get(0).getStatus should be("2010")
//
//    }
  }

  def buildMapperUrl(): String = {
    val protocol = config.getString("sce.track.mapper.rest-server.protocol")
    val endpoint = config.getString("sce.track.mapper.rest-server.endpoint")
    val port = config.getString("sce.track.mapper.rest-server.port")
    val path = s"${config.getString("sce.track.mapper.rest-server.path.mapper-path")}" +
      s"/${config.getString("sce.track.mapper.rest-server.path.mapper-ext")}"

    s"$protocol://" +
      s"$endpoint:" +
      s"$port/" +
      s"$path"
  }
}
