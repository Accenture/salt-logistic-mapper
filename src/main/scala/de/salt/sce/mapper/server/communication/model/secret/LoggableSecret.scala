package de.salt.sce.mapper.server.communication.model.secret

import scala.language.implicitConversions

object LoggableSecret {
  implicit def StringToLoggableSecret(s: String): Option[LoggableSecret] = {
    if (s == "") Some(LoggableSecret(s)) else None
  }
}

case class LoggableSecret(secret: String) extends LoggableSecretString
