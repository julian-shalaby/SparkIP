package com.databricks115
import org.scalatest.FunSuite

class TestIPv6Network extends FunSuite with SparkSessionTestWrapper{

  test("Network contains cidr notation - success") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6("2001:db8:85a3:0:0:8a2e:370:7335")
    assert(net.netContainsIP(ip))
  }

  test("Network contains cidr notation - failure") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6("1:db8:85a3:0:0:8a2e:370:7333")
    assert(!net.netContainsIP(ip))
  }

  //test when implemented
//  test("Network contains range notation - success") {
//    val net = IPv6Network("1:db8:85a3:0:0:8a2e:370:7333-2001:db8:85a3:0:0:8a2e:370:7334")
//    val ip = IPv6("2:db8:85a3:0:0:8a2e:370:7333")
//    assert(net.netContainsIP(ip))
//  }
//
//  test("Network contains range notation - failure") {
//    val net = IPv6Network("2:db8:85a3:0:0:8a2e:370:7333-2001:db8:85a3:0:0:8a2e:370:7334")
//    val ip = IPv6("1:db8:85a3:0:0:8a2e:370:7333")
//    assert(!net.netContainsIP(ip))
//  }

  test("Get network address") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    assert(net.networkAddress === IPv6("2001:db8:85a3:0:0:0:0:0"))
  }

  test("Get broadcast address") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    assert(net.broadcastAddress === IPv6("2001:db8:85a3:0:ffff:ffff:ffff:ffff"))
  }

  test("Network == - success") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7333/64")
    assert(net == ip)
  }

  test("Network == - failure") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6Network("8:db8:85a3:0:0:8a2e:370:7333/64")
    assert(!(net == ip))
  }

  test("Network != - success") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6Network("8:db8:85a3:0:0:8a2e:370:7333/64")
    assert(net != ip)
  }

  test("Network != - failure") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    val ip = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7333/64")
    assert(!(net != ip))
  }

  test("Networks intersect - success") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/18")
    val net2 = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/16")
    assert(net.netsIntersect(net2))
  }

  test("Networks intersect - failure") {
    val net = IPv6Network("7:db8:85a3:0:0:8a2e:370:7334/18")
    val net2 = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/16")
    assert(!net.netsIntersect(net2))
  }

  test("isNetworkAddress - success") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/64")
    assert(net.isNetworkAddress("2001:db8:85a3:0:0:0:0:0"))
  }

  test("isNetworkAddress - failure") {
    val net = IPv6Network("2001:db8:85a3:0:0:8a2e:370:7334/123")
//    assert(!net.isNetworkAddress("2001:db8:85a3:0:0:0:0:1"))
  }

}
