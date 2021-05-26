package com.databricks115
import org.scalatest.FunSuite

class TestIPNetwork extends FunSuite {

  test("Constructor") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net1.addrNumStart.right.get == 0)
    assert(net1.addrNumEnd.right.get == BigInt("5192296858534827628530496329220095"))

    assert(net2.addrNumStart.left.get == 3221225472L)
    assert(net2.addrNumEnd.left.get ==  3221291007L)

    assert(net3.addrNumStart.left.get == 3221225472L)
    assert(net3.addrNumEnd.left.get ==  3221291007L)
  }

  test("==") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net1 == net1)
    assert(net2 == net2)
    assert(net3 == net3)
    assert(!(net1 == net2))
  }

  test("!=") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net1 != net2)
    assert(!(net2 != net3))
  }

  test("<") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net2 <  net1)
    assert(net3 <  net1)
    assert(!(net3 <  net2))
  }

  test(">") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net1 >  net2)
    assert(net1 >  net3)
    assert(!(net2 >  net3))
  }

  test(">=") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net1 >=  net2)
    assert(net3 >=  net2)
    assert(net1 >= net1)
    assert(net2 >= net2)
  }

  test("<=") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("192.0.0.0/255.255.0.0")

    assert(net2 <=  net1)
    assert(net2 <=  net3)
    assert(net1 <= net1)
    assert(net2 <= net2)
  }

  test("sorted") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("192.0.0.0/16")
    val net3 = IPNetwork("191.0.0.0/255.255.0.0")
    val netseq = Seq(net1, net3, net2)

    assert(netseq.sorted == Seq(net3, net2, net1))
  }

  test("contains") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("0.0.0.0/16")
    val net3 = IPNetwork("0.0.0.0/255.255.0.0")

    assert(net1.contains("::21"))
    assert(!net1.contains("0.0.0.3"))
    assert(!net1.contains("2::21"))

    assert(net2.contains("0.0.0.6"))
    assert(!net2.contains("192.0.0.6"))
    assert(!net2.contains("::3"))

    assert(net3.contains("0.0.0.6"))
    assert(!net3.contains("192.0.0.6"))
    assert(!net3.contains("::3"))
  }

  test("nets intersect") {
    val net1 = IPNetwork("::/16")
    val net2 = IPNetwork("0.0.0.0/16")
    val net3 = IPNetwork("::/8")
    val net4 = IPNetwork("0.0.0.0/8")

    assert(net1.netsIntersect(net3))
    assert(net3.netsIntersect(net1))
    assert(!net3.netsIntersect(net2))

    assert(net2.netsIntersect(net4))
    assert(net4.netsIntersect(net2))
    assert(!net2.netsIntersect(net1))
  }

}
