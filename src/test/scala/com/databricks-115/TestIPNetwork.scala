package com.databricks115
import org.scalatest.FunSuite

class TestIPNetwork extends FunSuite with SparkSessionTestWrapper{
  test("Network CIDR Constructor - failure") {
    assertThrows[Exception] {
      val net = IPNetwork("192.161.150.78/21")
    }
  }
  
  test("Network contains cidr notation - success") {
    val net = IPNetwork("192.161.144.0/21")
    val ip = IPv4("192.161.145.1")
    assert(net.netContainsIP(ip))
  }

  test("Network contains cidr notation - failure") {
    val net = IPNetwork("191.161.144.0/21")
    val ip = IPv4("192.161.145.1")
    assert(!net.netContainsIP(ip))
  }

  test("Network contains subnet mask notation - success") {
    val net = IPNetwork("192.161.150.78/255.255.248.0")
    val ip = IPv4("192.161.145.1")
    assert(net.netContainsIP(ip))
  }

  test("Network contains subnet mask notation - failure") {
    val net = IPNetwork("191.161.150.78/255.255.248.0")
    val ip = IPv4("192.161.145.1")
    assert(!net.netContainsIP(ip))
  }

  test("Network contains address netmask string notation - success") {
    val net = IPNetwork("Address 192.161.150.78 Netmask 255.255.248.0")
    val ip = IPv4("192.161.145.1")
    assert(net.netContainsIP(ip))
  }

  test("Network contains address netmask string notation - failure") {
    val net = IPNetwork("Address 191.161.150.78 Netmask 255.255.248.0")
    val ip = IPv4("192.161.145.1")
    assert(!net.netContainsIP(ip))
  }

  test("Network contains range notation - success") {
    val net = IPNetwork("191.161.144.1-191.161.151.254")
    val ip = IPv4("191.161.150.1")
    assert(net.netContainsIP(ip))
  }

  test("Network contains range notation - failure") {
    val net = IPNetwork("191.161.144.1-191.161.151.254")
    val ip = IPv4("192.161.145.1")
    assert(!net.netContainsIP(ip))
  }

  test("Get network address 1") {
    val net = IPNetwork("191.161.0.0/16")
    assert(net.networkAddress === IPv4("191.161.0.0"))
  }

  test("Get network address 2") {
    val net = IPNetwork("191.161.0.0/16")
    assert(net.broadcastAddress === IPv4("191.161.255.255"))
  }

  test("Network == - success") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net == ip)
  }

  test("Network == - failure") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/23")
    assert(!(net == ip))
  }

  test("Network != - success") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/23")
    assert(net != ip)
  }

  test("Network != - failure") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net != ip))
  }

  test("Network < - success 1") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net < ip)
  }

  test("Network < - success 2") {
    val net = IPNetwork("192.0.0.0/18")
    val ip = IPNetwork("192.0.0.0/16")
    assert(net < ip)
  }

  test("Network < - failure 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.0.0/16")
    assert(!(net < ip))
  }

  test("Network < - failure 2") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.0.0/18")
    assert(!(net < ip))
  }

  test("Network > - success 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.0.0/16")
    assert(net > ip)
  }

  test("Network > - success 2") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.0.0/18")
    assert(net > ip)
  }

  test("Network > - failure 1") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net > ip))
  }

  test("Network > - failure 2") {
    val net = IPNetwork("192.0.0.0/18")
    val ip = IPNetwork("192.0.0.0/16")
    assert(!(net > ip))
  }

  test("Network <= - success 1") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net <= ip)
  }

  test("Network <= - success 2") {
    val net = IPNetwork("192.0.0.0/18")
    val ip = IPNetwork("192.0.0.0/18")
    assert(net <= ip)
  }

  test("Network <= - failure 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.0.0/16")
    assert(!(net <= ip))
  }

  test("Network <= - failure 2") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.0.0/18")
    assert(!(net <= ip))
  }

  test("Network >= - success 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.0.0/16")
    assert(net >= ip)
  }

  test("Network >= - success 2") {
    val net = IPNetwork("192.0.0.0/18")
    val ip = IPNetwork("192.0.0.0/18")
    assert(net >= ip)
  }

  test("Network >= - failure 1") {
    val net = IPNetwork("192.0.0.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net >= ip))
  }

  test("Network >= - failure 2") {
    val net = IPNetwork("192.0.0.0/18")
    val ip = IPNetwork("192.0.0.0/16")
    assert(!(net >= ip))
  }

  test("Networks intersect - success") {
    val net = IPNetwork("192.0.0.0/18")
    val net2 = IPNetwork("192.0.0.0/16")
    assert(net.netsIntersect(net2))
  }

  test("Networks intersect - failure") {
    val net = IPNetwork("191.0.0.0/18")
    val net2 = IPNetwork("192.0.0.0/16")
    assert(!net.netsIntersect(net2))
  }

  test("isNetworkAddress - success") {
    val net = IPNetwork("191.0.0.0/18")
    assert(net.isNetworkAddress("191.0.0.0"))
  }

  test("isNetworkAddress - failure") {
    val net = IPNetwork("191.0.0.0/18")
    assert(!net.isNetworkAddress("191.0.1.1"))
  }
}
