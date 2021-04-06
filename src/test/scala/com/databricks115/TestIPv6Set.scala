package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPv6Set extends FunSuite with BeforeAndAfter {
    var ipSet: IPv6Set = _

    before {
        ipSet = IPv6Set(Seq(IPv6Network("2001:0db8:85a3:0000::/64")))
    }

    test("String Sequence Constructor") {
        val _ = IPv6Set(Seq("1:db8:85a3:0:0:8a2e:370:7333", "2:db8:85a3:0:0:8a2e:370:7222"))
        succeed
    }

    test("IP Address Sequence Constructor") {
        val _ = IPv6Set(Seq(IPv6("1:db8:85a3:0:0:8a2e:370:7333"), IPv6("2:db8:85a3:0:0:8a2e:370:7222")))
        succeed
    }

    test("IP Network Constructor") {
        val _ = IPv6Set(IPv6Network("1:db8:85a3:0:0:8a2e:370:7333"))
        succeed
    }

    test("IP Address Constructor") {
        val _ = IPv6Set(IPv6("1:db8:85a3:0:0:8a2e:370:7333"))
        succeed
    }

    test("String Constructor") {
        val _ = IPv6Set("1:db8:85a3:0:0:8a2e:370:7333")
        succeed
    }

    test("RangeSet Constructor") {
        val _ = IPv6Set(ipSet.addrSet)
        succeed
    }

    test("Contains - success 1") {
        val ip = IPv6("2001:db8:85a3:0:0:0:0:1")
        assert(ipSet contains ip)
    }

    test("Contains - success 2") {
        val ip = "2001:db8:85a3:0:0:0:0:1"
        assert(ipSet contains ip)
    }

    test("Contains - success 3") {
        val net = IPv6Network("2001:0db8:85a3:0000::/70")
        assert(ipSet contains net)
    }

    test("Contains - failure 1") {
        val ip = IPv6("1000:8:90a4:0000::")
        assert(!(ipSet contains ip))
    }

    test("Contains - failure 2") {
        val net = IPv6Network("2000::/8")
        assert(!(ipSet contains net))
    }

    test("Apply - success") {
        val ip = IPv6("2001:0db8:85a3:0:0:0:0:1")
        assert(ipSet(ip))
    }

    test("Apply - failure") {
        val ip = IPv6("2000:8:90a4:0000::")
        assert(!(ipSet(ip)))
    }

    test("addOne - success 1") {
        val ip = IPv6("10::")
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne - success 2") {
        val ip = "10::"
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne - success 3") {
        val net = IPv6Network("1000::/8")
        ipSet addOne net
        assert(ipSet(net))
    }

    test("+= - success") {
        val ip = IPv6("10::")
        ipSet += ip
        assert(ipSet(ip))
    }

    test("addAll - success 1") {
        val ip = IPv6("10::")
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll - success 2") {
        val ip = "10::"
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll - success 3") {
        val net = "1000::/8"
        val newSet = Seq(net)
        ipSet addAll newSet
        assert(ipSet(net))
    }

    test("++= - success") {
        val ip = IPv6("10::")
        val newSet = Seq(ip)
        ipSet ++= newSet
        assert(ipSet(ip))
    }

    test("subtractOne - success 1") {
        val ip = IPv6("2001:0db8:85a3:0000:1::")
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne - success 2") {
        val ip = "2001:0db8:85a3:0000:1::"
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne - success 3") {
        val net = IPv6Network("2001:0db8:85a3:0000::/64")
        ipSet subtractOne net
        assert(ipSet.isEmpty)
    }

    test("-= - success") {
        val ip = IPv6("2001:0db8:85a3:0000:1::")
        ipSet -= ip
        assert(!ipSet(ip))
    }

    test("subtractAll - success 1") {
        val ip1 = IPv6("2001:0db8:85a3:0000:1::")
        val ip2 = IPv6("2001:0db8:85a3:0000:2::")
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("subtractAll - success 2") {
        val ip1 = "2001:0db8:85a3:0000:1::"
        val ip2 = "2001:0db8:85a3:0000:2::"
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("--= - success") {
        val ip1 = IPv6("2001:0db8:85a3:0000:1::")
        val ip2 = IPv6("2001:0db8:85a3:0000:2::")
        val newSet = Seq(ip1, ip2)
        ipSet --= newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }
    
    test("Intersect - success") {
        val largerSet = IPv6Set(Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("1001::")))
        val newSet = ipSet intersect largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(!newSet("1001::"))
    }

    test("& - success") {
        val largerSet = IPv6Set(Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("1001::")))
        val newSet = ipSet & largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(!newSet("1001::"))
    }

    test("Union - success") {
        val largerSet = IPv6Set(Seq(IPv6("2000::"), IPv6("3000::")))
        val newSet = ipSet union largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(newSet("2000::"))
        assert(newSet("3000::"))
    }

    test("| - success") {
        val largerSet = IPv6Set(Seq(IPv6("2000::"), IPv6("3000::")))
        val newSet = ipSet | largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(newSet("2000::"))
        assert(newSet("3000::"))
    }

    test("Diff - success") {
        val largerSet = IPv6Set(Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("3000::")))
        val newSet = largerSet diff ipSet 
        assert(newSet("3000::"))
        assert(!newSet("2001:db8:85a3:0:0:0:0:1"))
    }

    test("&~ - success") {
        val largerSet = IPv6Set(Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("3000::")))
        val newSet = largerSet &~ ipSet 
        assert(newSet("3000::"))
        assert(!newSet("2001:db8:85a3:0:0:0:0:1"))
    }
}