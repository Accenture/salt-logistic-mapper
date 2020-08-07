package de.salt.sce.mapper.server.util

import com.typesafe.config.{Config, ConfigFactory}

trait LazyConfig {
  protected lazy val config: Config = ConfigFactory.load
}
