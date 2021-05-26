package com.databricks115
import org.scalatest.FunSuite

class TestIPSet extends FunSuite {

  test("Constructor") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", List("::", "::5"))
    val ip1 = IPAddress("192.0.0.0")
    val ip2 = IPAddress("::")
    val net1 = IPNetwork("::/16")
    val set2 = IPSet(ip1, ip2, net1, Set("::", "::5"))
    val set3 = IPSet()

    assert(set1 == set2)
    assert(set1 != set3)
  }

  test("==") {
    val set1 = IPSet("::", "::/8", "192.0.0.0")
    val set2 = IPSet("192.0.0.0", "::", "::/8")

    assert(set1 == set2)
  }

  test("!=") {
    val set1 = IPSet("::", "::/8", "192.0.0.0")
    val set2 = IPSet("::", "::/8")

    assert(set1 != set2)
  }

  test("Add") {
    val set1 = IPSet()
    set1.add("192.0.0.0", "::", "2001::/16", List("::", "::5"))
    val ip1 = IPAddress("192.0.0.0")
    val ip2 = IPAddress("::")
    val net1 = IPNetwork("2001::/16")
    val set2 = IPSet()
    set2.add(ip1, ip2, net1, Set("::", "::5"))

    assert(set1 == set2)
  }

  test("Remove") {
    val set1 = IPSet("192.0.0.0", "::", "::/8", Set("::", "::5"))
    val set2 = IPSet()
    set1.remove("192.0.0.0", "::", "::/8", List("::", "::5"))

    assert(set1 == set2)
  }

  test("Contains") {
    val set1 = IPSet("192.0.0.0", "::", "2001::/16")

    assert(set1.contains("2001::7"))
    assert(set1.contains("2001::/16"))
  }

  test("Clear") {
    val set1 = IPSet("192.0.0.0", "::", "2001::/16")
    set1.clear()

    assert(set1 == IPSet())
  }

  test("isEmpty") {
    assert(IPSet().isEmpty)
  }

  test("!isEmpty") {
    assert(!IPSet("::").isEmpty)
  }

  test("returnAll") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("192.0.0.0", "::/16", "192.0.0.0/8", "5.0.0.0/12", "::")
    assert(set1.returnAll() == set2.returnAll())
  }

  test("intersection") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("::/16", "5.0.0.0/12", "::", "::5", "::6")

    assert(set1.intersection(set2) == IPSet("::/16", "5.0.0.0/12", "::"))
  }

  test("union") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("::/16", "5.0.0.0/12", "::", "::5", "::6")

    assert(set1.union(set2) == IPSet("::/16", "5.0.0.0/12", "::", "192.0.0.0", "::5", "::6", "192.0.0.0/8"))
  }

  test("diff") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12")
    val set2 = IPSet("::/16", "5.0.0.0/12", "::", "::5", "::6")

    assert(set1.diff(set2) == IPSet("192.0.0.0", "192.0.0.0/8"))
  }

  test("length") {
    val set1 = IPSet("192.0.0.0", "::", "::/16", "192.0.0.0/8", "5.0.0.0/12", Set("::", "::5", "::/8", "::/8"))

    assert(set1.length == 7)
  }

}
