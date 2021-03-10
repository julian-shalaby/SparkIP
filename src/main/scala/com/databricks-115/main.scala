package com.databricks115
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object main extends App {

  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._

  def timeFunc(funcToTime: () => Unit) {
    val t0 = System.nanoTime()
    funcToTime()
    val t1 = System.nanoTime()
    val elapsed = t1 - t0
    println(s"Elapsed time: $elapsed ns or ${elapsed/1000000000} sec")
  }

  val path = "src/test/scala/com/databricks-115/IPText.json"
  val IPv4DS: Dataset[IPv4] = spark.read.json(path).as[IPv4]

  val network1 = IPv4Network("192.0.0.0/17")
  //why does this take so long?
  timeFunc(() => IPv4DS.filter(ip => network1.netContainsIP(ip)).show())
  //but this is fast
  timeFunc(() => IPv4DS.filter(ip => ip.isMulticast).show())
  //there is a bottleneck in ip network

}
