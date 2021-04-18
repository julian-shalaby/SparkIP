package com.databricks115
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class TestIPv4SetPerf extends FunSuite with BeforeAndAfter {
  var ipList: List[IPv4] = _
  before {
    val r = scala.util.Random
    val ipListBuilder = List.newBuilder[IPv4]
    for (i <- 1 to 1000000) {
        ipListBuilder += IPv4(r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256))
    }
    ipList = ipListBuilder.result()
  }

  test("Contains") {
    val ipv4Set: IPv4Set = IPv4Set(Seq("192.168.0.2", "48.2.5.10", "10.0.1.2", "64.233.160.0/19", "207.126.144.0/20", "172.217.0.0/19"))
    time ("Contains (range set)") { ipList.filter(ip => ipv4Set(ip)) }
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