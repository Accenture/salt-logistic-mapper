package de.salt.sce.mapper.server.communication.actor

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.StatusCodes
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.MapperServiceClientTrack
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.{InternalResponse, MapperResponseProtocol}
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Formats}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Companion Object for MapperClient
 */
object MapperClientActor extends LazyLogging {
  final val Name: String = "mapper-mapper-client"

  def props: Props = Props(new MapperClientActor)
}

/**
 * Generic mapper client :
 * pushes data to Mapper server
 */
class MapperClientActor extends Actor with LazyLogging with LazyConfig {

  implicit val formats: Formats = DefaultFormats

  /**
   * Method handling the incoming request.
   *
   * @return Nothing. Sends answer back to sender.
   */
  def receive: Receive = {

    // productive case:
    // extract data and delegate to subclass to ask webservice
    case mapperRequest: MapperRequest =>
      logger.trace(s"Received mapping request [$mapperRequest]")
      val senderRef = sender()

      // Build answer and send it back to sender
      senderRef ! {
        try {
          doCreateResponse(mapperRequest) match {
            case Success(mapperResponseProtocol) =>
              // send response back to Microservice with status OK
              InternalResponse(
                id = mapperRequest.id,
                extResponse = mapperResponseProtocol,
                statusCode = StatusCodes.OK.intValue
              )

            case Failure(ex) =>
              // Failure in subclass. Connection could not be established. Send failure back to Microservice.
              logger.error(s"Exception [${ex.getClass}] occurred in subclass during mapping: message [${ex.getMessage}]")
              logger.error(ex.getStackTrace.map(_.toString).mkString("\n"))
              buildErrorResponse(mapperRequest, StatusCodes.InternalServerError.intValue)
          }
        } catch {
          case e: Exception =>
            logger.error(s"Exception [${e.getClass}] occurred during mapping: message [${e.getMessage}]")
            logger.error(e.getStackTrace.map(_.toString).mkString("\n"))
            buildErrorResponse(mapperRequest, StatusCodes.InternalServerError.intValue)
        }
      }
  }

  /**
   * Builds an error response based on the given request.
   * Uses the hash of the request, if there is any.
   * (Change this if we use multiple requests)
   *
   * @param mapperRequest complete given request
   * @param statusCode    http code of error
   * @return complete response protocol with error protocol
   */
  protected def buildErrorResponse(mapperRequest: MapperRequest, statusCode: Integer): InternalResponse = {
    InternalResponse(
      id = mapperRequest.id,
      extResponse = MapperResponseProtocol(
        success = mutable.HashMap(),
        error = mutable.HashMap()
      ),
      statusCode = statusCode
    )
  }

  /**
   * Method to create response.
   *
   * @param requestData incoming request data. Contains connection information.
   * @return Success: response containing mapped files.
   */
  protected def doCreateResponse(requestData: MapperRequest): Try[MapperResponseProtocol] = {
    Success(MapperServiceClientTrack.buildResponse(requestData, config))
  }
}
