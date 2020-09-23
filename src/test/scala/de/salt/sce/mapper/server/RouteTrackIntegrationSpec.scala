package de.salt.sce.mapper.server

import de.salt.sce.mapper.util.IntegrationTester


class RouteTrackIntegrationSpec extends IntegrationTester {
  override def beforeAll(): Unit = {
    logger.warn("Make sure Mapper is up and running! ")
  }

  "AMM" should {

    s"return a protocol echo response for authenticated POST requests" in {
      createAndCheckAuthRequest("amm")
    }
  }
}
