package com.databricks115
import org.scalatest.FunSuite

class TestIPNetwork extends FunSuite with SparkSessionTestWrapper{
  test("Network") {
    val test = IPNetwork("192.168.1.0/24")
  }
}
