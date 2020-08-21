package de.salt.sce.mapper.server.communication.client

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.MapperServiceClientTrack
import de.salt.sce.mapper.server.communication.model.Requests.{TrackProviderRequest, TrackRequest}
import de.salt.sce.mapper.server.communication.model.Responses.{InternalResponse, TrackResponseProtocol}
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Formats}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Companion Object for TrackClient
 */
object TrackClient extends LazyLogging {
  final val Name: String = s"track-mapper-client"

  def props: Props = Props(new TrackClient)
}


/**
 * Generic track client :
 * pushes data to Track server
 */
class TrackClient extends Actor with LazyLogging with LazyConfig {

  implicit val formats: Formats = DefaultFormats

  /**
   * Method handling the incoming request.
   *
   * @return Nothing. Sends answer back to sender.
   */
  def receive: Receive = {

    // productive case:
    // extract data and delegate to subclass to ask webservice
    case trackReq: TrackProviderRequest =>
      logger.trace(s"Received track request [$trackReq]")
      val senderRef = sender()

      // Build answer and send it back to sender
      senderRef ! {
        try {
          doCreateResponse(trackReq.header, trackReq) match {
            case Success(trackResponseProtocol) =>
              // send response back to Microservice with status OK
              InternalResponse(
                id = trackReq.id,
                extResponse = trackResponseProtocol,
                statusCode = StatusCodes.OK.intValue
              )

            case Failure(ex) =>
              // Failure in subclass. Connection could not be established. Send failure back to Microservice.
              logger.error(s"Exception [${ex.getClass}] occurred in subclass during track download: message [${ex.getMessage}]")
              logger.error(ex.getStackTrace.map(_.toString).mkString("\n"))
              buildErrorResponse(trackReq, StatusCodes.InternalServerError.intValue)
          }
        } catch {
          case e: Exception =>
            logger.error(s"Exception [${e.getClass}] occurred during track download: message [${e.getMessage}]")
            logger.error(e.getStackTrace.map(_.toString).mkString("\n"))
            buildErrorResponse(trackReq, StatusCodes.InternalServerError.intValue)
        }
      }
  }

  /**
   * Builds an error response based on the given request.
   * Uses the hash of the request, if there is any.
   * (Change this if we use multiple requests)
   *
   * @param trackRequest complete given request
   * @param statusCode   http code of error
   * @return complete response protocol with error protocol
   */
  protected def buildErrorResponse(trackRequest: TrackRequest, statusCode: Integer): InternalResponse = {
    InternalResponse(
      id = trackRequest.id,
      extResponse = TrackResponseProtocol(
        success = mutable.HashMap(),
        error = mutable.HashMap()
      ),
      statusCode = statusCode
    )
  }

  /**
   * Abstract method to create response. Redefine in subclass.
   * Contacts service provider, asks status of given reference IDs and returns status of IDs.
   *
   * @param header      incoming HTTP header
   * @param requestData incoming request data. Contains connection information.
   * @return Success: response containing status of reference IDs.
   *         Failure if connection could not be established.
   */
  protected def doCreateResponse(header: Seq[HttpHeader], requestData: TrackProviderRequest): Try[TrackResponseProtocol] = {
    Success(MapperServiceClientTrack.buildResponse(requestData, config))
  }

  protected def getHeader(headers: Seq[HttpHeader], headerName: String): String = {
    headers.find(h => h.name() == headerName) match {
      case Some(header) => header.value()
      case None => ""
    }
  }


}
