package com.databricks115

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

private case class SparkIPInit(spark: SparkSession)  {
  // Multicast
  spark.udf.register("isMulticast", udf((ip: String) => IPAddress(ip).isMulticast))
  // Private

  // Global

  // Link Local

  // LoopBack

  // Unspecified

  // IPv4 Mapped

  // IPv4 Translated

  // IPv4 IPv6 Translated

  // Teredo

  // 6to4

  // Reserved

  // Compressed

  // Exploded

  // IPv4

  // IPv6

  // IPv4 as num

  // IP as binary

  // Network Contains
  spark.udf.register("netContains", udf((ip: String, net: String) => IPNetwork(net).contains(IPAddress(ip))))

}

case object SparkIP {
  var spark: SparkSession = _
  var logLevel: String = _
  var setMap: scala.collection.mutable.Map[String, IPSet] = scala.collection.mutable.Map()

  def apply(ss: SparkSession, ll: String = null): Unit = {
    spark = ss
    SparkIPInit(spark)
    if (ll == null) {
      println("No log level specified for SparkIP. Setting log level to WARN.")
      logLevel = "WARN"
    }
    else logLevel = ll
    update_sets()
  }

  def update_sets(): Unit = {
    if (spark == null) return
    spark.sparkContext.setLogLevel("FATAL")
    spark.udf.register("setContains", udf((ip: String, set: String) => setMap(set) contains ip))
    spark.sparkContext.setLogLevel(logLevel)
  }

  def add(setToAdd: IPSet, setName: String): Unit = {
    setMap += (setName -> setToAdd)
    update_sets()
  }

  def remove(setName: String*): Unit = {
    setName.foreach(setMap -= _)
    update_sets()
  }

  def clear(): Unit = setMap.clear()

  def setsAvailable(): Unit = setMap.foreach(println)

  // Pure UDFs
  // Multicast
  def isMulticast: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isMulticast)

  // Set Contains
  def setContains(ipset: IPSet): UserDefinedFunction = udf((ip: String) => ipset contains ip)

}
