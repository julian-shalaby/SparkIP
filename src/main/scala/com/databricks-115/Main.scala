package com.databricks115
import org.apache.spark.sql.{Dataset, SparkSession}

object Main extends App {
  val spark = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._

  //load the json and create a view to query
  val path = "src/test/scala/com/databricks-115/IPText.json"
  val IPv4DS: Dataset[IPv4] = spark.read.json(path).as[IPv4]

  val network1 = IPv4Network("192.0.0.0/17")
  val network2 = IPv4Network("0.0.0.0-192.0.0.0")
  val network3 = IPv4Network("0.0.0.0/17")

  //why does this take so long?
  IPv4DS.filter(ip => network1.netContainsIP(ip)).show()
  //and this
  IPv4DS.filter(ip => network3.netContainsIP(ip)).show()
  //but this is fast
  IPv4DS.filter(ip => ip.isMulticast).show()
  //and this is fast
  IPv4DS.filter(ip => network2.netContainsIP(ip)).show()

}