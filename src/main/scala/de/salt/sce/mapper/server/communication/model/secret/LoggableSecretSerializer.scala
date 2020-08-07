package de.salt.sce.mapper.server.communication.model.secret

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString


/**
 * Custom serializer for LoggableSecretString
 * ensures that json sequence
 * { ..., "password" : "abcd", ...}
 * is translated to scala
 * password = Some(LoggableSecret("abcd"))
 *
 * dito for sshKey and sshKeyPass
 */
class LoggableSecretSerializer extends CustomSerializer[LoggableSecretString](_ => ( {
  case JString(password) =>
    LoggableSecret(password)
}, {
  case secretString: LoggableSecretString =>
    JString(secretString.secret)
}))
