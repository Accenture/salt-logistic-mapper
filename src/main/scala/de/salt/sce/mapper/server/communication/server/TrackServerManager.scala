package de.salt.sce.mapper.server.communication.server

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer.create
import com.typesafe.config.ConfigObject
import com.typesafe.scalalogging.LazyLogging
import de.salt.sce.mapper.Bootstrap.config
import de.salt.sce.mapper.communication.getconfigs.ConfigCopy
import de.salt.sce.mapper.server.util.LazyConfig
import akka.http.javadsl.Http.get;
/**
 * Track-Server Manager Companion Object
 */
object TrackServerManager {
  final val Name: String = "track-server-manager"

  def props: Props = Props(new TrackServerManager)
}

/**
 * Track-Server Manager:
 * Parent of Track Server
 */
class TrackServerManager extends Actor with LazyLogging with LazyConfig  {
  override def preStart(): Unit = {

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

    logger.debug("Creating Track Server")
    context.actorOf(TrackServer.props, TrackServer.Name)
  }

  override def postStop(): Unit =
    logger.debug("Track Server Manager stopped")

  def receive: Receive = Actor.emptyBehavior
}
