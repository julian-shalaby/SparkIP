package com.databricks115

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

case class SparkIPInit(spark: SparkSession)  {
  // Multicast
  spark.udf.register("isMulticast", udf((IPAddr: String) => IPAddress(IPAddr).isMulticast))

  // netContains
  spark.udf.register("netContains", udf((ip: String, net: String) => IPNetwork(net).contains(IPAddress(ip))))
}
