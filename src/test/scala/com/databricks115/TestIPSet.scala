package com.databricks115
import org.scalatest.FunSuite

class TestIPSet extends FunSuite {

  test("Constructor") {
    val set1 = IPSet("192.0.0.0", "::", "::/16")
    val ip1 = IPAddress("192.0.0.0")
    val ip2 = IPAddress("::")
    val net1 = IPNetwork("::/16")
    val set2 = IPSet(ip1, ip2, net1)
    val set3 = IPSet()

    assert(set1.ipMap == set2.ipMap)
    assert(set1.ipMap != set3.ipMap)
  }

  test("Add") {
    val set1 = IPSet()
    set1.add("192.0.0.0", "::", "2001::/16")
    val ip1 = IPAddress("192.0.0.0")
    val ip2 = IPAddress("::")
    val net1 = IPNetwork("2001::/16")
    val set2 = IPSet()
    set2.add(ip1, ip2, net1)

    assert(set1 == set2)
  }

  test("Remove") {
    val set1 = IPSet("192.0.0.0", "::", "::/8")
    set1.remove("192.0.0.0", "::", "::/8")

    assert(set1 == IPSet())
  }

  test("Contains") {
    val set1 = IPSet("192.0.0.0", "::", "2001::/16")

    assert(set1.contains("2001::7"))
    assert(set1.contains("2001::/16"))
  }

  test("==") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("5.0.0.0/12", "192.0.0.0/8", "192.0.0.0", "::/16", "::")
    assert(set1 == set2)
  }

  test("!=") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("192.0.0.0", "::/16", "::")
    assert(set1 != set2)
  }

}
