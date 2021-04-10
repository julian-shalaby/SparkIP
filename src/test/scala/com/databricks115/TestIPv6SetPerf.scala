package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPv6SetPerf extends FunSuite with BeforeAndAfter {
  var ipList: List[IPv6] = _
  before {
    val r = scala.util.Random
    val ipListBuilder = List.newBuilder[IPv6]
    for (i <- 1 to 1000000) {
      // creating ipv6 addresses is slow (18 sec), look into a faster method
      ipListBuilder += IPv6(List.fill(8)("").map(str => r.nextInt(65536).toHexString).mkString(":"))
    }
    ipList = ipListBuilder.result()
  }

  test("Contains") {
    val ipv4Set: IPv6Set = IPv6Set(Seq("2001:4860:4000:0:0:0:0:0", "b0ac:63c6:5964:a5af:5ab3:7576:4c27:c24c", "1001:1:80ab:beaf:dead::", "2404:6800:4000::/36", "2607:f8b0:4000::/36", "c7fd:665b:3712::/48"))

    time ("Contains") { ipList.filter(ip => ipv4Set(ip)) }

    succeed
  }

  def time[T](msg: String)(block: => T): Unit = {
    var totalRunTime: Long = 0
    for (i <- 1 to 10) {
      val t1 = System.currentTimeMillis
      block
      val t2 = System.currentTimeMillis
      totalRunTime += (t2 - t1)
    }
    println(msg + ": " + totalRunTime / 10 + " ms avg")
  }
}
