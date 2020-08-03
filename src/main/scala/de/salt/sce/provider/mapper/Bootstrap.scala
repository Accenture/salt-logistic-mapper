package de.salt.sce.provider.mapper

import de.salt.sce.provider.mapper.client.MapperServiceClient
import de.salt.sce.provider.model.{Provider, ProviderStarter}

object Bootstrap extends App with ProviderStarter {

  private val providerName = "mapper"
  Provider.setProviderName(providerName)

  start(providerName, MapperServiceClient.props)

}