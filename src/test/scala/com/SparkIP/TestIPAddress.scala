package com.SparkIP
import org.scalatest.FunSuite

class TestIPAddress extends FunSuite {

  test("Constructor") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")

    assert(ip1.addrNum.right.get == 9)
    assert(ip2.addrNum.left.get == 9)
  }

  test("==") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")

    assert(ip1 == ip1)
    assert(ip2 == ip2)
    assert(!(ip1 == ip2))
  }

  test("!=") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")

    assert(ip1 != ip2)
    assert(!(ip1 != ip1))
  }

  test("<") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")
    val ip3 = IPAddress("::10")

    assert(ip2 <  ip1)
    assert(ip1 <  ip3)
  }

  test(">") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")
    val ip3 = IPAddress("::10")

    assert(ip1 >  ip2)
    assert(ip3 >  ip1)
  }

  test(">=") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")
    val ip3 = IPAddress("::10")

    assert(ip1 >=  ip2)
    assert(ip3 >=  ip1)
    assert(ip1 >= ip1)
    assert(ip2 >= ip2)
  }

  test("<=") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")
    val ip3 = IPAddress("::10")

    assert(ip2 <  ip1)
    assert(ip1 <  ip3)
    assert(ip1 <= ip1)
    assert(ip2 <= ip2)
  }

  test("sorted") {
    val ip1 = IPAddress("::9")
    val ip2 = IPAddress("0.0.0.9")
    val ip3 = IPAddress("::10")
    val ipseq = Seq(ip1, ip3, ip2)

    assert(ipseq.sorted == Seq(ip2, ip1, ip3))
  }

  test("mask") {
    val ip1 = IPAddress("2:5::6")
    val ip2 = IPAddress("192.0.5.1")

    assert(ip1.mask(16) == IPAddress("2::"))
    assert(ip2.mask(16) == IPAddress("192.0.0.0"))
    assert(ip2.mask("255.255.0.0") == IPAddress("192.0.0.0"))
  }

}
