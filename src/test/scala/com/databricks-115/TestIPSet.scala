package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPSet extends FunSuite with SparkSessionTestWrapper with BeforeAndAfter {
    var ipSet: IPSet = _

    before {
        ipSet = new IPSet(Seq(IPv4("212.222.131.201")))
    }
    
    // test("String Sequence Constructor") {
    //     val _ = IPSet(Seq("212.222.131.201", "212.222.131.200"))
    //     succeed
    // }

    test("Contains - success") {
        val ip = IPv4("212.222.131.201")
        assert(ipSet contains ip)
    }

    test("Contains - failure") {
        val ip = IPv4("212.222.131.0")
        assert(!(ipSet contains ip))
    }
}