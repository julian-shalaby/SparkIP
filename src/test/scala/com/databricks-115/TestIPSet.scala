package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPSet extends FunSuite with SparkSessionTestWrapper with BeforeAndAfter {
    var ipSet: IPSet = _

    before {
        ipSet = IPSet(Seq(IPNetwork("212.222.131.201/16")))
    }

    test("String Sequence Constructor") {
        val _ = IPSet(Seq("212.222.131.201", "212.222.131.200"))
        succeed
    }

    test("IP Address Sequence Constructor") {
        val _ = IPSet(Seq(IPv4("212.222.131.201"), IPv4("212.222.131.200")))
        succeed
    }

    test("IP Network Constructor") {
        val _ = IPSet(IPNetwork("212.222.131.201"))
        succeed
    }

    test("IP Address Constructor") {
        val _ = IPSet(IPv4("212.222.131.201"))
        succeed
    }

    test("String Constructor") {
        val _ = IPSet("212.222.131.201")
        succeed
    }

    test("RangeSet Constructor") {
        val _ = IPSet(ipSet.addrSet)
        succeed
    }

    test("Contains - success 1") {
        val ip = IPv4("212.222.131.201")
        assert(ipSet contains ip)
    }

    test("Contains - success 2") {
        val ip = "212.222.131.201"
        assert(ipSet contains ip)
    }

    test("Contains - success 3") {
        val net = IPNetwork("212.222.131.201/24")
        assert(ipSet contains net)
    }

    test("Contains - failure 1") {
        val ip = IPv4("212.0.0.0")
        assert(!(ipSet contains ip))
    }

    test("Contains - failure 2") {
        val net = IPNetwork("212.222.131.201/8")
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
        val net = IPNetwork("1.0.0.1/8")
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
        val net = "1.0.0.1/8"
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
        val net = IPNetwork("212.222.131.201/16")
        ipSet subtractOne net
        assert(ipSet.isEmpty)
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
        val largerSet = IPSet(Seq(IPv4("212.222.131.201"), IPv4("1.0.0.1")))
        val newSet = ipSet intersect largerSet
        assert(newSet("212.222.131.201"))
        assert(!newSet("1.0.0.1"))
    }

    test("& - success") {
        val largerSet = IPSet(Seq(IPv4("212.222.131.201"), IPv4("1.0.0.1")))
        val newSet = ipSet & largerSet
        assert(newSet("212.222.131.201"))
        assert(!newSet("1.0.0.1"))
    }

    test("Union - success") {
        val largerSet = IPSet(Seq(IPv4("1.0.0.1"), IPv4("2.2.2.2")))
        val newSet = ipSet union largerSet
        assert(newSet("212.222.131.201"))
        assert(newSet("1.0.0.1"))
        assert(newSet("2.2.2.2"))
    }

    test("| - success") {
        val largerSet = IPSet(Seq(IPv4("1.0.0.1"), IPv4("2.2.2.2")))
        val newSet = ipSet | largerSet
        assert(newSet("212.222.131.201"))
        assert(newSet("1.0.0.1"))
        assert(newSet("2.2.2.2"))
    }

    test("Diff - success") {
        val largerSet = IPSet(Seq(IPv4("212.222.131.201"), IPv4("2.2.2.2")))
        val newSet = largerSet diff ipSet 
        assert(newSet("2.2.2.2"))
    }

    test("&~ - success") {
        val largerSet = IPSet(Seq(IPv4("212.222.131.201"), IPv4("2.2.2.2")))
        val newSet = largerSet &~ ipSet
        assert(newSet("2.2.2.2"))
    }
}