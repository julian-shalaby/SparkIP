package com.databricks115
import org.scalatest.FunSuite

class TestIPSet extends FunSuite {

  test("Constructor") {
    val set1 = IPSet("192.0.0.0", "::", "225.0.0.0/8")
    val ip1 = IPv4("192.0.0.0")
    val ip2 = IPv6("::")
    val net1 = IPv4Network("225.0.0.0/8")
    val set2 = IPSet(ip1, ip2, net1)
    val set3 = IPSet()

    assert(set1.ipMap == set2.ipMap)
    assert(set1.ipMap != set3.ipMap)
  }

  test("Add") {
    val set1 = IPSet()
    set1.add("192.0.0.0", "::", "::/16")
    val ip1 = IPv4("192.0.0.0")
    val ip2 = IPv6("::")
    val net1 = IPv6Network("::/16")
    val set2 = IPSet()
    set2.add(ip1, ip2, net1)

    assert(set1.ipMap == set2.ipMap)
  }

  test("Remove") {
    val set1 = IPSet("192.0.0.0", "::")
    val set2 = IPSet()
    set1.remove("192.0.0.0", "::")

    assert(set1.ipMap == set2.ipMap)
  }

}
