package de.salt.sce.mapper.util

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.slf4j.LoggerFactory.getLogger

class ScalaToolSpec extends FlatSpec
  with Matchers with BeforeAndAfterAll {
  private val logger = getLogger(this.getClass)

  "ScalaTool" should "truncate correctly" in {
    ScalaTool.truncate("123456&amp;", 10) should be("123456")
    ScalaTool.truncate("12345&amp;", 10) should be("12345&amp;")
    ScalaTool.truncate("12345&amp;&auml;", 20) should be("12345&amp;&auml;")
    ScalaTool.truncate("12345&amp;&auml;&Auml;", 25) should be("12345&amp;&auml;&Auml;")
    ScalaTool.truncate("12345&amp;&aum", 20) should be("12345&amp;")
    ScalaTool.truncate("123&&amp;", 8) should be("123")
    ScalaTool.truncate("123&&amp;", 10) should be("123&&amp;")
    //                  123456789012345678901234567890123456789012345678901234567890
  }

  "ScalaTool" should "truncate leading and trailing spaces" in {
    //ScalaTool.truncate("test eins ", 10) should be("test eins")
    ScalaTool.truncate("  OFFICE TRADE LTD.                            ", 40) should be("OFFICE TRADE LTD.")
    ScalaTool.truncate("    ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN    ", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
  }

  "ScalaTool" should "truncate long strings correctly" in {
    ScalaTool.truncate("OFFICE TRADE L &amp;&auml;&Auml;&amp;&auml;", 40) should be("OFFICE TRADE L &amp;&auml;&Auml;&amp;")
  }

  "ScalaTool" should "truncate SIMPLE long strings correctly" in {
    ScalaTool.truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNop", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    ScalaTool.truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNo", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    ScalaTool.truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    ScalaTool.truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLM", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLM")
  }


  "ScalaTool" should "not truncate SIMPLE short strings" in {
    ScalaTool.truncate("  ABCDEFGHI", 40) should be("ABCDEFGHI")
  }
}