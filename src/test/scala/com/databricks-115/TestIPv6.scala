package com.databricks115
import org.scalatest.FunSuite
import java.math.BigInteger

class TestIPv6 extends FunSuite with SparkSessionTestWrapper{

  test("Mask IP - success") {
    val maskTest = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val maskTest2 = IPv6("2001:0db8:0000:0000:0000:0000:0000:0000")
    assert(maskTest.mask(32) == maskTest2)
  }

  test("Mask IP - failure") {
    val maskTest = IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val maskTest2 = IPv6("2001:0db8:0000:0000:0000:0000:0000:0000")
    assert(maskTest.mask(17) != maskTest2)
  }
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

  /*
  IPv4 translated addresses:
  ::ffff:0:0.0.0.0 to ::ffff:0:255.255.255.255
  18446462598732840960 to 18446462603027808255
  */
  test("IPv4 translated addresses") {
    val IPv4TranslatedIPs = List(
      IPv6("::ffff:0:0.0.0.0"),
      IPv6("::ffff:0:255.255.255.255")
    )
    assert(IPv4TranslatedIPs.forall(ip => ip.isIPv4Translated))
  }
  test("Not IPv4 translated addresses") {
    val IPv4TranslatedIPs = List(
      IPv6("::fff:0:0.0.0.0"),
      IPv6("::ffff:f:255.255.255.255")
    )
    assert(IPv4TranslatedIPs.forall(ip => !ip.isIPv4Translated))
  }

  /*
IPv4/IPv6 translation:
  64:ff9b::0.0.0.0 to 64:ff9b::255.255.255.255
  524413980667603649783483181312245760 to 524413980667603649783483185607213055
 */
  test("IPv4/IPv6 translated addresses") {
    val IPv4IPv6TranslatedIPs = List(
      IPv6("64:ff9b::0.0.0.0"),
      IPv6("64:ff9b::255.255.255.255")
    )
    assert(IPv4IPv6TranslatedIPs.forall(ip => ip.isIPv4IPv6Translated))
  }
  test("Not IPv4/IPv6 translated addresses") {
    val IPv4IPv6TranslatedIPs = List(
      IPv6("::fff:0:0.0.0.0"),
      IPv6("::ffff:f:255.255.255.255")
    )
    assert(IPv4IPv6TranslatedIPs.forall(ip => !ip.isIPv4IPv6Translated))
  }

  /*
Teredo:
  2001:: to 2001::ffff:ffff:ffff:ffff:ffff:ffff
  42540488161975842760550356425300246528 to 42540488241204005274814694018844196863
  */
  test("Teredo addresses") {
    val teredoIPs = List(
      IPv6("2001::"),
      IPv6("2001::ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(teredoIPs.forall(ip => ip.isTeredo))
  }
  test("Not Teredo addresses") {
    val teredoIPs = List(
      IPv6("0"),
      IPv6("ffff::ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(teredoIPs.forall(ip => !ip.isTeredo))
  }

/*
6to4:
  2002:: to 2002:ffff:ffff:ffff:ffff:ffff:ffff:ffff
  42545680458834377588178886921629466624 to 42550872755692912415807417417958686719
   */
  test("6to4 addresses") {
    val s6to4IPs = List(
      IPv6("2002::"),
      IPv6("2002:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(s6to4IPs.forall(ip => ip.is6to4))
  }
  test("Not 6to4 addresses") {
    val s6to4IPs = List(
      IPv6("::"),
      IPv6("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")
    )
    assert(s6to4IPs.forall(ip => !ip.is6to4))
  }

  test("compareTo - success 1") {
    val ip1 = IPv6("ff00:0:0:0:0:0:0:1")
    val ip2 = IPv6("ff00:0:0:0:0:0:0:0")
    assert((ip1 compareTo ip2) == 1)
  }

  test("compareTo - success 2") {
    val ip1 = IPv6("ff00:0:0:0:0:0:0:0")
    val ip2 = IPv6("ff00:0:0:0:0:0:0:1")
    assert((ip1 compareTo ip2) == -1)
  }

  test("compare - success 1") {
    val ip1 = IPv6("ff00:0:0:0:0:0:0:1")
    val ip2 = IPv6("ff00:0:0:0:0:0:0:0")
    assert((ip1 compare ip2) == 1)
  }

  test("compare - success 2") {
    val ip1 = IPv6("ff00:0:0:0:0:0:0:0")
    val ip2 = IPv6("ff00:0:0:0:0:0:0:1")
    assert((ip1 compare ip2) == -1)
  }

  test("6to4") {
    val ip1 = IPv6("2002:49e7:a9b2:0:0:0:0:0")
    assert(ip1.sixToFour == IPv4("73.231.169.178"))
  }

  test("IPv4Mapped") {
    val ip1 = IPv6("0:0:0:0:0:ffff:49e7:a9b2")
    assert(ip1.IPv4Mapped == IPv4("73.231.169.178"))
  }

  test("teredo") {
    val ip1 = IPv6("2001:0:49e7:a9b2:0:0:0:0")
    assert(ip1.teredo == IPv4("73.231.169.178"))
  }

}
