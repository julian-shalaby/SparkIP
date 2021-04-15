package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPv4Setv2 extends FunSuite with BeforeAndAfter {
  var ipSet: IPv4Setv2 = _

  before {
    ipSet = IPv4Setv2(IPv4Network("212.222.0.0/16"))
  }

  test("IPv4 constructor - success") {
    val ipSet_ = IPv4Setv2(IPv4("212.222.0.0"))
    succeed
  }

  test("String constructor - success") {
    val ipSet_ = IPv4Setv2("212.222.0.0/16")
    succeed
  }

  test("IPv4Network Seq constructor - success") {
    val ipSet_ = IPv4Setv2(Seq(IPv4Network("212.222.0.0/16")))
    succeed
  }

  test("String Seq constructor - success") {
    val ipSet_ = IPv4Setv2(Seq("212.222.0.0/16"))
    succeed
  }

  test("IPv4 Seq constructor - success") {
    val ipSet_ = IPv4Setv2(Seq(IPv4("212.222.0.0")))
    succeed
  }

  test("Contains IPv4Network - success 1") {
    val ip = IPv4Network("212.222.0.0/16")
    assert(ipSet contains ip)
  }

  test("Contains IPv4Network - failure 1") {
    val ip = IPv4Network("212.0.0.0/8")
    assert(!(ipSet contains ip))
  }

  test("Contains IPv4 - success 1") {
    val ip = IPv4("212.222.0.0")
    assert(ipSet contains ip)
  }

  test("Contains IPv4 - failure 1") {
    val ip = IPv4("1.0.0.1")
    assert(!(ipSet contains ip))
  }

  test("Contains String - success 1") {
    val ip = "212.222.0.0/16"
    assert(ipSet contains ip)
  }

  test("Contains String - failure 1") {
    val ip = "212.0.0.0/8"
    assert(!(ipSet contains ip))
  }

}
