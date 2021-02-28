package com.databricks115
import org.scalatest.FunSuite
import java.math.BigInteger

class TestIPv6Address extends FunSuite with SparkSessionTestWrapper{
  test("== - success") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip == ip2)
  }

  test("== - failure") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip != ip2)
  }

  test("< - success") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip < ip2)
  }

  test("< - failure") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip < ip2))
  }

  test("<= - success") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip <= ip2)
  }

  test("<= - failure") {
    val ip = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip <= ip2))
  }

  test("> - success") {
    val ip = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip > ip2)
  }

  test("> - failure") {
    val ip = IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip > ip2))
  }

  test(">= - success") {
    val ip = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip >= ip2)
  }

  test(">= - failure") {
    val ip = IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip >= ip2))
  }

  test("IP to BigInt - success") {
    val ip = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip.addrBI == new BigInteger("63808414359686959922502208113658529928"))
  }

  test("IP to BigInt - failure") {
    val ip = IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip.addrBI != new BigInteger("63808414359686959922502208113658529920"))
  }

  /*
Link Local:
  fe80:: to febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff
  338288524927261089654018896841347694592 to 338620831926207318622244848606417780735
 */
  test("Link Local Addresses") {
    val linkLocalIPs = List(
      IPv6("fe80:0:0:0:0:0:0:0"),
      IPv6("febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(linkLocalIPs.forall(ip => ip.isLinkLocal))
  }
  test("Not Link Local Addresses") {
    val linkLocalIPs = List(
      IPv6("fe70:0:0:0:0:0:0:0"),
      IPv6("feff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(linkLocalIPs.forall(ip => !ip.isLinkLocal))
  }

  /*
Unspecified:
  ::
  0
 */
  test("Unspecified Addresses") {
    val unspecifiedIP = IPv6("0:0:0:0:0:0:0:0")
    assert(unspecifiedIP.isUnspecified)
  }
  test("Not Unspecified Addresses") {
    val unspecifiedIP = IPv6("1:0:0:0:0:0:0:0")
    assert(!unspecifiedIP.isUnspecified)
  }

  /*
Loopback:
  0:0:0:0:0:0:0:1
  1
 */
  test("Loopback Addresses") {
    val loopBackIP = IPv6("0:0:0:0:0:0:0:1")
    assert(loopBackIP.isLoopback)
  }
  test("Not Loopback Addresses") {
    val loopBackIP = IPv6("1:0:0:0:0:0:0:1")
    assert(!loopBackIP.isLoopback)
  }

  /*
Unique:
  fc00:: to fdff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
  334965454937798799971759379190646833152 to 337623910929368631717566993311207522303
 */
  test("Unique Addresses") {
    val uniqueLocalIPs = List(
      IPv6("fc00:0:0:0:0:0:0:0"),
      IPv6("fdff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(uniqueLocalIPs.forall(ip => ip.isUniqueLocal))
  }
  test("Not Unique Addresses") {
    val uniqueLocalIPs = List(
      IPv6("fb00:0:0:0:0:0:0:0"),
      IPv6("feff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(uniqueLocalIPs.forall(ip => !ip.isUniqueLocal))
  }

  /*
Multicast:
  ff00:: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
  338953138925153547590470800371487866880 to 340282366920938463463374607431768211455
 */
  test("Multicast Addresses") {
    val multicastIPs = List(
      IPv6("ff00:0:0:0:0:0:0:0"),
      IPv6("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(multicastIPs.forall(ip => ip.isMulticast))
  }
  test("Not Multicast Address") {
    val multicastIP = IPv6("fe00:0:0:0:0:0:0:0")
    assert(!multicastIP.isMulticast)
  }

}
