package de.salt.sce.mapper.actor

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.server.util.LazyConfig
import org.json4s.{DefaultFormats, Serialization, native}
import akka.http.javadsl.Http.get;
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import akka.stream.ActorMaterializer.create
import com.typesafe.config.ConfigObject
import de.salt.sce.mapper.Bootstrap.config
import de.salt.sce.mapper.communication.getconfigs.ConfigCopy


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
    case "" =>
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
