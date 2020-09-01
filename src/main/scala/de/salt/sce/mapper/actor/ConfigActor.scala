package de.salt.sce.mapper.actor

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, Props}
import akka.http.javadsl.Http.get
import akka.stream.ActorMaterializer.create
import akka.util.Timeout
import com.typesafe.config.ConfigObject
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.communication.getconfigs.ConfigCopy
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Serialization, native}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration


object ConfigActor {
  final val Name: String = "smooks-configs"

  def props: Props = Props(new ConfigActor)
}


class ConfigActor extends Actor with LazyLogging with LazyConfig {
  implicit val s: Serialization = native.Serialization
  implicit val f: DefaultFormats = DefaultFormats
  implicit val timeout: Timeout = Timeout(Duration(config.getInt(s"sce.track.mapper.rest-server.timeout-sec"), SECONDS))
  implicit val ec: ExecutionContext = context.dispatcher

  def receive: Receive = {
    case "INIT_CONFIG" =>
    val httpClient = get(context.system)
    val actorMaterializer = create(context.system)
    config.getConfig(s"sce.track.mapper.smooks.clients").root().forEach {
      case (name: String, configObject: ConfigObject) =>
        ConfigCopy.copy(
          httpClient,
          actorMaterializer,
          configObject.toConfig,
          name,
          config.getString(s"sce.track.mapper.smooks.config-files-path"),
          config.getInt(s"sce.track.mapper.smooks.request_timeout_mills")
        )
    }
  }
}
