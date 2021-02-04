package com.databricks115

import org.scalatest.FunSuite

class TestIPAddress extends FunSuite with SparkSessionTestWrapper{
 
    test("isIP - valid IP") {
        var ip = new IPAddress("192.168.0.1")
        assert(ip.isIP)
    }

    test("isIP - invalid IP") {
        var ip = new IPAddress("192.168.0")
        assert(!ip.isIP)
    }
}
