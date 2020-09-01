package de.salt.sce.mapper.util

import de.salt.sce.mapper.util.ScalaTool.truncate
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ScalaToolSpec extends FlatSpec
  with Matchers with BeforeAndAfterAll {

  "ScalaTool" should "truncate correctly" in {
    truncate("123456&amp;", 10) should be("123456")
    truncate("12345&amp;", 10) should be("12345&amp;")
    truncate("12345&amp;&auml;", 20) should be("12345&amp;&auml;")
    truncate("12345&amp;&auml;&Auml;", 25) should be("12345&amp;&auml;&Auml;")
    truncate("12345&amp;&aum", 20) should be("12345&amp;")
    truncate("123&&amp;", 8) should be("123")
    truncate("123&&amp;", 10) should be("123&&amp;")
  }

  "ScalaTool" should "truncate leading and trailing spaces" in {
    //ScalaTool.truncate("test eins ", 10) should be("test eins")
    truncate("  OFFICE TRADE LTD.                            ", 40) should be("OFFICE TRADE LTD.")
    truncate("    ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN    ", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
  }

  "ScalaTool" should "truncate long strings correctly" in {
    truncate("OFFICE TRADE L &amp;&auml;&Auml;&amp;&auml;", 40) should be("OFFICE TRADE L &amp;&auml;&Auml;&amp;")
  }

  "ScalaTool" should "truncate SIMPLE long strings correctly" in {
    truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNop", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNo", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN")
    truncate("  ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLM", 40) should be("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLM")
  }


  "ScalaTool" should "not truncate SIMPLE short strings" in {
    truncate("  ABCDEFGHI", 40) should be("ABCDEFGHI")
  }
}