package de.salt.sce.mapper.util

import java.util.UUID

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpCredentials}
import com.typesafe.config.ConfigException
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, Serialization, native}

import scala.concurrent.ExecutionContext

object Util extends LazyLogging with LazyConfig {

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats

  def buildClientRequest(mapperRequest: MapperRequest, uri: String)
                        (implicit ec: ExecutionContext): Option[HttpRequest] = {

    try {

      Some(HttpRequest(
        HttpMethods.POST,
        uri = uri,
        entity = serializeEntity(mapperRequest),
        headers = List(
          headers.Authorization(
            buildCredentials()
          )
        )
      )
      )
    } catch {
      case e: ConfigException =>
        logger.warn(s"No rest client configuration found for dispatcher: " + e.getMessage)
        None
    }
  }

  def serializeEntity(mapperRequest: MapperRequest)(implicit ec: ExecutionContext): RequestEntity = {
    //    Await.result(Marshal(dispatchRequest).to[RequestEntity], 1.second)
    HttpEntity(
      MediaTypes.`application/json`,
      Serialization.write(mapperRequest)
    )
  }

  def buildCredentials(): HttpCredentials =
    BasicHttpCredentials(
      config.getString("sce.track.mapper.rest-server.auth.username"),
      config.getString("sce.track.mapper.rest-server.auth.password")
    )

  def generateHash(): String = UUID.randomUUID().toString
}
