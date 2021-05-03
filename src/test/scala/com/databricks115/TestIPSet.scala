package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPSet extends FunSuite with BeforeAndAfter {
    var ipSet: IPSet = _

    before {
        ipSet = IPSet()
        ipSet += IPv4Network("212.222.0.0/16")
        ipSet += IPv6Network("2001:0db8:85a3:0000::/64")
    }

    test("isEmpty - empty set") {
        val set = IPSet()
        assert(set isEmpty)
    }

    test("isEmpty - non-empty set") {
        val set = IPSet()
        set += IPv4("192.168.0.1")
        assert(!(set isEmpty))
    }

    // IPv4

    test("Contains - success 1") {
        val ip = IPv4("212.222.131.201")
        assert(ipSet contains ip)
    }

    test("Contains - success 2") {
        val ip = "212.222.131.201"
        assert(ipSet contains ip)
    }

    test("Contains - success 3") {
        val net = IPv4Network("212.222.131.0/24")
        assert(ipSet contains net)
    }

    test("Contains - failure 1") {
        val ip = IPv4("212.0.0.0")
        assert(!(ipSet contains ip))
    }

    test("Contains - failure 2") {
        val net = IPv4Network("212.0.0.0/8")
        assert(!(ipSet contains net))
    }

    test("Apply - success") {
        val ip = IPv4("212.222.131.201")
        assert(ipSet(ip))
    }

    test("Apply - failure") {
        val ip = IPv4("212.0.0.0")
        assert(!(ipSet(ip)))
    }

    test("addOne - success 1") {
        val ip = IPv4("1.0.0.1")
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne - success 2") {
        val ip = "1.0.0.1"
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne - success 3") {
        val net = IPv4Network("1.0.0.0/8")
        ipSet addOne net
        assert(ipSet(net))
    }

    test("+= - success") {
        val ip = IPv4("1.0.0.1")
        ipSet += ip
        assert(ipSet(ip))
    }

    test("addAll - success 1") {
        val ip = IPv4("1.0.0.1")
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll - success 2") {
        val ip = "1.0.0.1"
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll - success 3") {
        val net = "1.0.0.0/8"
        val newSet = Seq(net)
        ipSet addAll newSet
        assert(ipSet(net))
    }

    test("++= - success") {
        val ip = IPv4("1.0.0.1")
        val newSet = Seq(ip)
        ipSet ++= newSet
        assert(ipSet(ip))
    }

    test("subtractOne - success 1") {
        val ip = IPv4("212.222.131.201")
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne - success 2") {
        val ip = "212.222.131.201"
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne - success 3") {
        val net = IPv4Network("212.222.0.0/16")
        ipSet subtractOne net
        assert(!ipSet(net))
    }

    test("-= - success") {
        val ip = IPv4("212.222.131.201")
        ipSet -= ip
        assert(!ipSet(ip))
    }

    test("subtractAll - success 1") {
        val ip1 = IPv4("212.222.131.201")
        val ip2 = IPv4("212.222.131.200")
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("subtractAll - success 2") {
        val ip1 = "212.222.131.201"
        val ip2 = "212.222.131.200"
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("--= - success") {
        val ip1 = IPv4("212.222.131.201")
        val ip2 = IPv4("212.222.131.200")
        val newSet = Seq(ip1, ip2)
        ipSet --= newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }
    
    test("Intersect - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("212.222.131.201"), IPv4("2.0.0.1"))
        val newSet = ipSet intersect largerSet
        assert(newSet("212.222.131.201"))
        assert(!newSet("2.0.0.1"))
    }

    test("& - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("212.222.131.201"), IPv4("1.0.0.1"))
        val newSet = ipSet & largerSet
        assert(newSet("212.222.131.201"))
        assert(!newSet("1.0.0.1"))
    }

    test("Union - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("1.0.0.1"), IPv4("2.2.2.2"))
        val newSet = ipSet union largerSet
        assert(newSet("212.222.131.201"))
        assert(newSet("1.0.0.1"))
        assert(newSet("2.2.2.2"))
    }

    test("| - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("1.0.0.1"), IPv4("2.2.2.2"))
        val newSet = ipSet | largerSet
        assert(newSet("212.222.131.201"))
        assert(newSet("1.0.0.1"))
        assert(newSet("2.2.2.2"))
    }

    test("Diff - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("212.222.131.201"), IPv4("2.2.2.2"))
        val newSet = largerSet diff ipSet
        assert(newSet("2.2.2.2"))
    }

    test("&~ - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv4("212.222.131.201"), IPv4("2.2.2.2"))
        val newSet = largerSet &~ ipSet
        assert(newSet("2.2.2.2"))
    }

    // IPv6

    test("Contains IPv6 - success 1") {
        val ip = IPv6("2001:db8:85a3:0:0:0:0:1")
        assert(ipSet contains ip)
    }

    test("Contains IPv6 - success 2") {
        val ip = "2001:db8:85a3:0:0:0:0:1"
        assert(ipSet contains ip)
    }

    test("Contains IPv6 - success 3") {
        val net = IPv6Network("2001:0db8:85a3:0000::/70")
        assert(ipSet contains net)
    }

    test("Contains IPv6 - failure 1") {
        val ip = IPv6("1000:8:90a4:0000::")
        assert(!(ipSet contains ip))
    }

    test("Contains IPv6 - failure 2") {
        val net = IPv6Network("2000::/8")
        assert(!(ipSet contains net))
    }

    test("Apply IPv6 - success") {
        val ip = IPv6("2001:0db8:85a3:0:0:0:0:1")
        assert(ipSet(ip))
    }

    test("Apply IPv6 - failure") {
        val ip = IPv6("2000:8:90a4:0000::")
        assert(!(ipSet(ip)))
    }

    test("addOne IPv6 - success 1") {
        val ip = IPv6("10::")
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne IPv6 - success 2") {
        val ip = "10::"
        ipSet addOne ip
        assert(ipSet(ip))
    }

    test("addOne IPv6 - success 3") {
        val net = IPv6Network("1000::/8")
        ipSet addOne net
        assert(ipSet(net))
    }

    test("+= IPv6 - success") {
        val ip = IPv6("10::")
        ipSet += ip
        assert(ipSet(ip))
    }

    test("addAll IPv6 - success 1") {
        val ip = IPv6("10::")
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll IPv6 - success 2") {
        val ip = "10::"
        val newSet = Seq(ip)
        ipSet addAll newSet
        assert(ipSet(ip))
    }

    test("addAll IPv6 - success 3") {
        val net = "1000::/8"
        val newSet = Seq(net)
        ipSet addAll newSet
        assert(ipSet(net))
    }

    test("++= IPv6 - success") {
        val ip = IPv6("10::")
        val newSet = Seq(ip)
        ipSet ++= newSet
        assert(ipSet(ip))
    }

    test("subtractOne IPv6 - success 1") {
        val ip = IPv6("2001:0db8:85a3:0000:1::")
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne IPv6 - success 2") {
        val ip = "2001:0db8:85a3:0000:1::"
        ipSet subtractOne ip
        assert(!ipSet(ip))
    }

    test("subtractOne IPv6 - success 3") {
        val net = IPv6Network("2001:0db8:85a3:0000::/64")
        ipSet subtractOne net
        assert(!ipSet(net))
    }

    test("-= IPv6 - success") {
        val ip = IPv6("2001:0db8:85a3:0000:1::")
        ipSet -= ip
        assert(!ipSet(ip))
    }

    test("subtractAll IPv6 - success 1") {
        val ip1 = IPv6("2001:0db8:85a3:0000:1::")
        val ip2 = IPv6("2001:0db8:85a3:0000:2::")
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("subtractAll IPv6 - success 2") {
        val ip1 = "2001:0db8:85a3:0000:1::"
        val ip2 = "2001:0db8:85a3:0000:2::"
        val newSet = Seq(ip1, ip2)
        ipSet subtractAll newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }

    test("--= IPv6 - success") {
        val ip1 = IPv6("2001:0db8:85a3:0000:1::")
        val ip2 = IPv6("2001:0db8:85a3:0000:2::")
        val newSet = Seq(ip1, ip2)
        ipSet --= newSet
        assert(!ipSet(ip1))
        assert(!ipSet(ip2))
    }
    
    test("Intersect IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("1001::"))
        val newSet = ipSet intersect largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(!newSet("1001::"))
    }

    test("& IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("1001::"))
        val newSet = ipSet & largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(!newSet("1001::"))
    }

    test("Union IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2000::"), IPv6("3000::"))
        val newSet = ipSet union largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(newSet("2000::"))
        assert(newSet("3000::"))
    }

    test("| IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2000::"), IPv6("3000::"))
        val newSet = ipSet | largerSet
        assert(newSet("2001:db8:85a3:0:0:0:0:1"))
        assert(newSet("2000::"))
        assert(newSet("3000::"))
    }

    test("Diff IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("3000::"))
        val newSet = largerSet diff ipSet 
        assert(newSet("3000::"))
        assert(!newSet("2001:db8:85a3:0:0:0:0:1"))
    }

    test("&~ IPv6 - success") {
        val largerSet = IPSet()
        largerSet ++= Seq(IPv6("2001:db8:85a3:0:0:0:0:1"), IPv6("3000::"))
        val newSet = largerSet &~ ipSet 
        assert(newSet("3000::"))
        assert(!newSet("2001:db8:85a3:0:0:0:0:1"))
    }
}