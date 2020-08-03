package de.salt.sce.provider.geo_fr

import de.salt.sce.provider.fex_ws_de.communication.client.MapperServiceClient
import de.salt.sce.provider.model.{Provider, ProviderStarter}

object Bootstrap extends App with ProviderStarter {

  private val providerName = "mapper"
  Provider.setProviderName(providerName)

  start(providerName, MapperServiceClient.props)

}