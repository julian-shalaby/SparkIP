package com.databricks115
import org.scalatest.FunSuite

class TestIPv6Address extends FunSuite with SparkSessionTestWrapper{
  test("== - success") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip == ip2)
  }

  test("== - failure") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip != ip2)
  }

  test("< - success") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip < ip2)
  }

  test("< - failure") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip < ip2))
  }

  test("<= - success") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip <= ip2)
  }

  test("<= - failure") {
    val ip = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip <= ip2))
  }

  test("> - success") {
    val ip = new IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip > ip2)
  }

  test("> - failure") {
    val ip = new IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip > ip2))
  }

  test(">= - success") {
    val ip = new IPv6("3001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(ip >= ip2)
  }

  test(">= - failure") {
    val ip = new IPv6("1001:db8:3333:4444:5555:6666:7777:8888")
    val ip2 = new IPv6("2001:db8:3333:4444:5555:6666:7777:8888")
    assert(!(ip >= ip2))
  }

}
