package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPSet extends FunSuite with SparkSessionTestWrapper with BeforeAndAfter {
    var ipSet: IPSet = _

    before {
        ipSet = IPSet(Seq(IPv4("212.222.131.201")))
    }

    test("String Sequence Constructor") {
        val _ = IPSet(Seq("212.222.131.201", "212.222.131.200"))
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

    test("Contains - failure") {
        val ip = IPv4("212.222.131.0")
        assert(!(ipSet contains ip))
    }

    test("Apply - success") {
        val ip = IPv4("212.222.131.201")
        assert(ipSet(ip))
    }

    test("Apply - failure") {
        val ip = IPv4("212.222.131.0")
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

    test("++= - success") {
        val ip = IPv4("1.0.0.1")
        val newSet = Seq(ip)
        ipSet ++= newSet
        assert(ipSet(ip))
    }

    test("subtractOne - success 1") {
        val ip = IPv4("212.222.131.201")
        ipSet subtractOne ip
        assert(ipSet.isEmpty)
    }

    test("subtractOne - success 2") {
        val ip = "212.222.131.201"
        ipSet subtractOne ip
        assert(ipSet.isEmpty)
    }

    test("-= - success") {
        val ip = IPv4("212.222.131.201")
        ipSet -= ip
        assert(ipSet.isEmpty)
    }

    test("subtractAll - success 1") {
        val ip = IPv4("212.222.131.201")
        val newSet = Seq(ip)
        ipSet subtractAll newSet
        assert(ipSet.isEmpty)
    }

    test("subtractAll - success 2") {
        val ip = "212.222.131.201"
        val newSet = Seq(ip)
        ipSet subtractAll newSet
        assert(ipSet.isEmpty)
    }

    test("--= - success") {
        val ip = IPv4("212.222.131.201")
        val newSet = Seq(ip)
        ipSet --= newSet
        assert(ipSet.isEmpty)
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