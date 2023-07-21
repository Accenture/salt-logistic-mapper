package de.salt.sce.mapper.util

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.communication.model.MapperRequest
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Formats, Serialization, native}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS, _}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

trait IntegrationTester extends AnyWordSpec with Matchers with ScalatestRouteTest with LazyConfig with LazyLogging {

  implicit val s: Serialization = native.Serialization
  implicit val formats: Formats = DefaultFormats
  implicit val testTimeout: Timeout = Timeout(30 seconds)

  def createAndCheckAuthRequest(mapperRequest: MapperRequest, mapperUri: String): InternalResponse = {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._ // should be visible only in this method where no deserialization to string is performed
    logger.debug(s"${mapperRequest.serviceName} should return a protocol echo response for authenticated POST requests to [/$mapperUri]")

    val mappingHttpRequest =
      Util.buildClientRequest(
        mapperRequest,
        mapperUri
      ).getOrElse(fail("Request could not be built"))

    logger.debug(s"Sending request $mappingHttpRequest")

    val response = Await.result(Http().singleRequest(mappingHttpRequest), testTimeout.duration)
    response.status should equal(StatusCodes.OK)

    Try(Await.result(Unmarshal(response.entity).to[InternalResponse], Duration(1, SECONDS))) match {
      case Success(responseProtocol) =>
        logger.debug("Response: " + response)
        responseProtocol
      case Failure(error) =>
        logger.debug(error.getMessage)
        fail(error.getMessage)
    }
  }
}
