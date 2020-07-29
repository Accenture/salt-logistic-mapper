package de.salt.sce.provider.fex_ws_de.communication.client

import akka.actor.Props
import akka.http.scaladsl.model.HttpHeader
import com.fedex.ws.track.v18.TrackService
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.provider.fex_ws_de.WebServiceClientTrack.buildUtilObjectResponse
import de.salt.sce.provider.model.communication.client.TrackClient
import de.salt.sce.provider.model.communication.model.RequestData
import de.salt.sce.provider.model.pojo.UtilObjectResponse

import scala.collection.JavaConverters._
import scala.util.Try

/**
 * Companion Object for PushClient
 */
object FexTrackClient extends LazyLogging {
  def props: Props = Props(new FexTrackClient)
}

/**
 * Fedex Client Track:
 * pushes data to Track server
 */
class FexTrackClient extends TrackClient {

  override def doCreateResponse(header: Seq[HttpHeader], requestData: RequestData, refIds: Array[String]): Try[UtilObjectResponse] = {
    Try(buildUtilObjectResponse(
      new ResponseToUtilObjectConverter(),
      new ParcelServiceClient(),
      new ParcelServiceRequestBuilder(config, requestData),
      new TrackService().getTrackServicePort(),
      refIds.toBuffer.asJava,
      config.getInt("sce.track.fex_ws_de.transfer_count")
    ))
  }
}
