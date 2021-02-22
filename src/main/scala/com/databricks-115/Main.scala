package com.databricks115
import org.apache.spark.sql.SparkSession
import scala.collection.mutable.ListBuffer

object Main extends App {
  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master", "local")
    .getOrCreate()

  val path = "src/main/scala/com/databricks-115/IPText.json"
  val IPAddressDF = spark.read.json(path)

  val IPs = spark.read.json(path).collect.flatMap(_.toSeq)
  var IPNetworks = new ListBuffer[IPNetwork]()
  IPs.foreach(IP => IPNetworks += IPNetwork(IP.asInstanceOf[String]))

  IPNetworks.foreach(IP => IP.displayAll())



}