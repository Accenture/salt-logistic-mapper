package de.salt.sce.mapper.server.communication.model

import akka.http.scaladsl.model.HttpHeader

object Requests {

  trait Request

  trait TrackRequest extends Request {
    def id: String

    def configName: String

    def lines: Map[String, String]

    def header: Seq[HttpHeader] = Nil
  }

  /**
   * Generic Track Request
   */
  case class TrackProviderRequest(id: String,
                                  configName: String,
                                  lines: Map[String, String],
                                  override val header: Seq[HttpHeader] = Nil) extends TrackRequest

}
