package de.salt.sce.provider.mapper.client

import akka.actor.Props
import akka.http.scaladsl.model.HttpHeader
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.provider.mapper.MapperServiceClientTrack.buildUtilObjectResponse
import de.salt.sce.provider.model.communication.client.TrackClient
import de.salt.sce.provider.model.communication.model.RequestData
import de.salt.sce.provider.model.pojo.UtilObjectResponse

import scala.util.Try

/**
 * Companion Object for PushClient
 */
object MapperServiceClient extends LazyLogging {
  def props: Props = Props(new MapperServiceClient)
}

/**
 * Mapper Client Track:
 * pushes data to Track server
 */
class MapperServiceClient extends TrackClient {

  override def doCreateResponse(header: Seq[HttpHeader], requestData: RequestData, refIds: Array[String]): Try[UtilObjectResponse] = {
    Try(buildUtilObjectResponse())
  }
}
