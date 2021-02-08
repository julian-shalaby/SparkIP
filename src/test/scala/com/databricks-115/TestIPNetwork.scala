package com.databricks115
import org.scalatest.FunSuite

class TestIPNetwork extends FunSuite with SparkSessionTestWrapper{
  test("Network") {
    val test = IPNetwork("192.168.150.78/18")
    println(test.networkAddress(test.addr))
    println(test.broadcastAddress(test.addr))
  }
}
