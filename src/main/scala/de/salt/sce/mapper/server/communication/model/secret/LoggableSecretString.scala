package de.salt.sce.mapper.server.communication.model.secret

/**
 * Holds a secret string.
 * In method toString, the secret is hashed.
 */
trait LoggableSecretString {

  def secret: String

  override def toString: String = {
    s"Hashed: [${secret.hashCode.toString}]"
  }
}
