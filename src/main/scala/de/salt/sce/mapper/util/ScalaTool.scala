package de.salt.sce.mapper.util

import de.salt.sce.mapper.exception.MoveException
import org.slf4j.LoggerFactory.getLogger

/**
 * Tools in Scala
 *
 * @author WRH
 * @since 3.0.1
 */
object ScalaTool {
  private val logger = getLogger(this.getClass);

  /**
   * Generate random exception
   */
  def generateRandomException(message: String, edcid: String = "", actorName: String = ""): Unit =
    if (java.util.concurrent.ThreadLocalRandom.current.nextDouble() < 0.2)
      throw new MoveException(message, edcid, actorName)

  /**
   * Truncates a string with regard to positions of & and ;
   * <br/><br/>
   * Warning: The count of & and ; is not checked:
   * {{{ScalaTool.truncate("123&&amp;", 10) should be("123&&amp;")}}}
   * <br/><br/>
   * Examples:
   * {{{
   * ScalaTool.truncate("123456&amp;", 10) should be("123456")
   * ScalaTool.truncate("12345&amp;", 10) should be("12345&amp;")
   * ScalaTool.truncate("12345&amp;&auml;", 20) should be("12345&amp;&auml;")
   * ScalaTool.truncate("12345&amp;&auml;&Auml;", 25) should be("12345&amp;&auml;&Auml;")
   * ScalaTool.truncate("12345&amp;&aum", 20) should be("12345&amp;")
   * ScalaTool.truncate("123&&amp;", 8) should be("123")
   * }}}
   *
   * @param source    Source string
   * @param charLimit Maximum limit of output characters in the string
   * @return Escaped string
   * @author wrh
   * @since 3.1.6
   */
  def truncate(source: String, charLimit: Int): String = {
    val trimmedSource = source.trim
    logger.debug("Input.length: " + trimmedSource.length)
    val lastSuffixIndex = trimmedSource.lastIndexOf(';', charLimit - 1)
    logger.debug("lastSuffixIndex: " + lastSuffixIndex)

    val nextPrefixIndex = trimmedSource.indexOf('&', lastSuffixIndex)
    logger.debug("nextPrefixIndex: " + nextPrefixIndex)

    if (lastSuffixIndex < 0 && nextPrefixIndex > 0) {
      trimmedSource.substring(0, nextPrefixIndex)
    }
    else if (nextPrefixIndex < 0 || nextPrefixIndex >= charLimit) {
      val limit = if (trimmedSource.length > charLimit) charLimit else trimmedSource.length
      trimmedSource.substring(0, limit)
    }
    else {
      trimmedSource.substring(0, lastSuffixIndex + 1)
    }

  }
}