package com.databricks115
import org.scalatest.FunSuite

class TestIPv6Network extends FunSuite with SparkSessionTestWrapper{
  test("cidr notation") {
    val net = IPv6Network("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff/16")
    println(net.bigIntegerToIPv6(net.addrBIStart))
    println(net.bigIntegerToIPv6(net.addrBIEnd))
  }
}
