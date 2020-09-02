package de.salt.sce.mapper.server.communication.model

import scala.collection.mutable

object MapperResponses {

  case class MapperResponseProtocol(
                                    success: mutable.Map[String, String],
                                    error: mutable.Map[String, String]
                                  ) {
    /**
     * Override for logging:
     * Case classes provide default toString, but Arrays don't
     *
     * @return String containing content of protocols
     */
    override def toString: String = {
      s"MapperResponseProtocol(" +
        s"Error: [${error.mkString(",")}], " +
        s"Success: [${success.mkString(",")}]" +
        s")"
    }
  }

  case class InternalResponse(id: String,
                              cvsResponse: Option[MapperResponseProtocol],
                              edifactResponse: Option[MapperResponseProtocol],
                              statusCode: Integer
                             )

}