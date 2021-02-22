package com.databricks115
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

object Main extends App {
  val spark = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  //load the json and create a view to query
  val path = "src/main/scala/com/databricks-115/IPText.json"
  val IPv4DF = spark.read.json(path)
  IPv4DF.createOrReplaceTempView("IPv4")

  // function and function registration to check if the IP address is in the IP network
  val IPNetUDF = udf((IPAddr: String, IPNet: String) => IPNetwork(IPNet).netContainsIP(IPv4(IPAddr)))
  spark.udf.register("IPNetUDF", IPNetUDF)

  //query to test the function
  spark.sql("SELECT * FROM IPv4 WHERE IPNetUDF(IPAddress, '192.0.0.0/24')").show()

}