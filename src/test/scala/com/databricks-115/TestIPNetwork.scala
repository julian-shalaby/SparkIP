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

  test("Network == - success") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net == ip)
  }

  test("Network == - failure") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/23")
    assert(!(net == ip))
  }

  test("Network != - success") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/23")
    assert(net != ip)
  }

  test("Network != - failure") {
    val net = IPNetwork("192.0.2.1/24")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net != ip))
  }

  test("Network < - success 1") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net < ip)
  }

  test("Network < - success 2") {
    val net = IPNetwork("192.0.2.0/18")
    val ip = IPNetwork("192.0.2.0/16")
    assert(net < ip)
  }

  test("Network < - failure 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/16")
    assert(!(net < ip))
  }

  test("Network < - failure 2") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/18")
    assert(!(net < ip))
  }

  test("Network > - success 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/16")
    assert(net > ip)
  }

  test("Network > - success 2") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/18")
    assert(net > ip)
  }

  test("Network > - failure 1") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net > ip))
  }

  test("Network > - failure 2") {
    val net = IPNetwork("192.0.2.0/18")
    val ip = IPNetwork("192.0.2.0/16")
    assert(!(net > ip))
  }

  test("Network <= - success 1") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(net <= ip)
  }

  test("Network <= - success 2") {
    val net = IPNetwork("192.0.2.0/18")
    val ip = IPNetwork("192.0.2.1/18")
    assert(net <= ip)
  }

  test("Network <= - failure 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/16")
    assert(!(net <= ip))
  }

  test("Network <= - failure 2") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/18")
    assert(!(net <= ip))
  }

  test("Network >= - success 1") {
    val net = IPNetwork("192.0.2.0/24")
    val ip = IPNetwork("192.0.2.0/16")
    assert(net >= ip)
  }

  test("Network >= - success 2") {
    val net = IPNetwork("192.0.2.0/18")
    val ip = IPNetwork("192.0.2.1/18")
    assert(net >= ip)
  }

  test("Network >= - failure 1") {
    val net = IPNetwork("192.0.2.0/16")
    val ip = IPNetwork("192.0.2.0/24")
    assert(!(net >= ip))
  }

  test("Network >= - failure 2") {
    val net = IPNetwork("192.0.2.0/18")
    val ip = IPNetwork("192.0.2.0/16")
    assert(!(net >= ip))
  }

}
