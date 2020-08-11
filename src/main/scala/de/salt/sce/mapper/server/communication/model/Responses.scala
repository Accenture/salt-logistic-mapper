package de.salt.sce.mapper.server.communication.model

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import de.salt.sce.mapper.model.TrackContract

object Responses {

  case class TrackResponseProtocol(
                                    success: Map[String, String] ,
                                    error: Map[String, String]
                                  ) {
    /**
     * Override for logging:
     * Case classes provide default toString, but Arrays don't
     *
     * @return String containing content of protocols
     */
    override def toString: String = {
      s"TrackResponseProtocol(" +
        s"Error: [${error.mkString(",")}], " +
        s"Success: [${success.mkString(",")}]" +
        s")"
    }
  }

  case class InternalResponse(id: String,
                              extResponse: TrackResponseProtocol,
                              statusCode: StatusCode = StatusCodes.OK,
                             )

}