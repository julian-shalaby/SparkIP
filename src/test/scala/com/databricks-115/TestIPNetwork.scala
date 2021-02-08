package com.databricks115
import org.scalatest.FunSuite

class TestIPNetwork extends FunSuite with SparkSessionTestWrapper{
  test("Network contains - success") {
    val net = IPNetwork("192.161.150.78/21")
    val ip = IPAddress("192.161.145.1")
    assert(net.netContainsIP(ip))
  }

  test("Network contains - failure") {
    val net = IPNetwork("191.161.150.78/21")
    val ip = IPAddress("192.161.145.1")
    assert(!net.netContainsIP(ip))
  }

  test("Network is same - success") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net == ip)
  }

  test("Network is same - failure") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/23")
    assert(net != ip)
  }
}
