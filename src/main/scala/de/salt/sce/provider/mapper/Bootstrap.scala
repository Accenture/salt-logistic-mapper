package de.salt.sce.provider.geo_fr

import de.salt.sce.provider.geo_fr.communication.client.GeodisTrackClient
import de.salt.sce.provider.model.{Provider, ProviderStarter}

object Bootstrap extends App with ProviderStarter  {

  private val providerName = "mapper"
  Provider.setProviderName(providerName)

  start(providerName, GeodisTrackClient.props)

}