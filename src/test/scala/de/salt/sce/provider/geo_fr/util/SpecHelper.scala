package de.salt.sce.provider.geo_fr.util

import java.io.{ByteArrayInputStream, IOException, ObjectInputStream}
import java.util
import java.util.zip.GZIPInputStream

import akka.actor.ActorSystem
import de.salt.sce.provider.geo_fr.communication.client.GeodisTrackClient
import de.salt.sce.provider.model.communication.client.TrackClient
import de.salt.sce.provider.model.communication.model.secret.{LoggableSecret, LoggableSecretPassword}
import de.salt.sce.provider.model.communication.model.{KeyValue, RequestData, TrackContract}
import de.salt.sce.provider.model.util.Tool
import de.salt.sce.provider.model.{ActorService, Provider}
import org.apache.commons.codec.binary.Hex.decodeHex

object SpecHelper {
  def beforeAll(system: ActorSystem): Unit = {
    Provider.setProviderName("geo_fr")
    TrackClient.subClassProps = GeodisTrackClient.props
    ActorService.setActorSystem(system)
    ActorService.createActorHierarchy()
  }

  def buildRequestData(): RequestData = {
    RequestData(
      hash = "RBR",
      host = "https://espace-client.geodis.com/services",
      port = 8082,
      remotePath = "",
      fileCompressed = "X",
      remoteFilename = "",
      encoding = "", // 3 Fedex mock tracking numbers: 449044304137821,149331877648230,122816215025810
      payload = "504B0304140000000800E6799B505EAFBCE3260000002F000000070000006D795F6461746105C1490100200C0330433C7AB115FFC6489287C408BD150FF36C76775219875239E2856E890F504B01021300140000000800E6799B505EAFBCE3260000002F0000000700000000000000000000000000000000006D795F64617461504B05060000000001000100350000004B0000000000",
      custprops =  List(
        KeyValue("service", "api/zoomclient/recherche-envoi"),
        KeyValue("api-key", "616b6d161cc342b092b6e192ea659349"),
        KeyValue("id", "EGGERROL")))
  }

  @SuppressWarnings(Array("unchecked"))
  @throws[IOException]
  @throws[ClassNotFoundException]
  def decompress(bytes: Array[Byte]): util.List[TrackContract] = {
    val bais: ByteArrayInputStream = new ByteArrayInputStream(bytes)
    val gzipIn: GZIPInputStream = new GZIPInputStream(bais)
    val objectIn: ObjectInputStream = new ObjectInputStream(gzipIn)
    val trackContracts: util.List[TrackContract] = objectIn.readObject.asInstanceOf[util.List[TrackContract]]
    objectIn.close()
    trackContracts
  }

  def decompressPayloadToJson(payload: String): String = {
    val decodedPayload = decodeHex(payload.toCharArray)
    val decompressedPayload = Tool.decompressPayload(decodedPayload)
    new String(decompressedPayload)
  }

}