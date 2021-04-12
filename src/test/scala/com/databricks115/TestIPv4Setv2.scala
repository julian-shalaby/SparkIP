package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPv4Setv2 extends FunSuite with BeforeAndAfter {
  var ipSet: IPv4Setv2[IPv4Network] = _

  before {
    ipSet = IPv4Setv2(IPv4Network("212.222.0.0/16"))
  }

  test("IPv4 constructor - success") {
    val ipSet_ = IPv4Setv2(IPv4("212.222.0.0"))
    succeed
  }

  test("String constructor - success") {
    val ipSet_ = IPv4Setv2("212.222.0.0/16")
  }

  test("Contains - success 1") {
    val ip = IPv4Network("212.222.0.0/16")
    assert(ipSet contains ip)
  }
}
