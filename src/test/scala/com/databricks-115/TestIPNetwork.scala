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
}
