package com.databricks115
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

object Main extends App {
  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master", "local")
    .getOrCreate()

  val path = "src/main/scala/com/databricks-115/IPText.json"
  val IPv4DF = spark.read.json(path)

  IPv4DF.createOrReplaceTempView("IPv4")

  // UDF in a WHERE clause
  val IPNetUDF = udf((x: String, y: String) => IPNetwork(x).netContainsIP(IPv4(y)))
  spark.udf.register("IPNetUDF", IPNetUDF)
  spark.sql("SELECT * FROM IPv4 WHERE IPNetUDF('192.0.0.0/24', IPAddress)").show()

}