package de.salt.sce.mapper.util

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.server.util.LazyConfig
import org.apache.commons.io.IOUtils
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS, _}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

trait IntegrationTester extends WordSpec with Matchers with ScalatestRouteTest with LazyConfig with LazyLogging {

  protected val mapperUri: String  = buildMapperUrl()

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats

  implicit val testTimeout: Timeout = Timeout(30 seconds)

  def createAndCheckAuthRequest(microserviceName: String): Unit = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed
    logger.debug(s"$microserviceName should return a protocol echo response for authenticated POST requests to [/$mapperUri]")

    val file: String = "P0815-STAT_IFTSTA-4.txt"
    val content: String = IOUtils.toString(this.getClass.getResourceAsStream(s"/smooks/amm/$file"), UTF_8)

    val mapperRequest = MapperRequest(
      id = UUID.randomUUID().toString,
      serviceName = "amm",
      configFile = "config-amm.xml",
      messageType = "edifact",
      encoding = "UTF-8",
      files = Map(
        file -> content,
        "Unknown" -> "Unknown format"
      )
    )
    val pingDispatchHttpRequest =
      Util.buildClientRequest(
        mapperRequest,
        mapperUri
      ).getOrElse(fail("Request could not be built"))

    logger.debug(s"Sending request $pingDispatchHttpRequest")

    val response = Await.result(Http().singleRequest(pingDispatchHttpRequest), testTimeout.duration)
    response.status should equal(StatusCodes.OK)

    Try(Await.result(Unmarshal(response.entity).to[InternalResponse], Duration(1, SECONDS))) match {
      case Success(responseProtocol) =>
        logger.debug("Response: " + response)

        responseProtocol.edifactResponse.get.success.size should be(1)
        responseProtocol.csvResponse should be(Option.empty)

      case Failure(error) =>
        logger.debug(error.getMessage)
        fail(error.getMessage)
    }
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
