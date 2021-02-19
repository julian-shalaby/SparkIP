package com.databricks115
import org.apache.spark.sql.SparkSession

object Main extends App {
  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master", "local")
    .getOrCreate()

  val IPAddressDF = spark.read
    .option("inferSchema", "true")
    .json("src/main/scala/com/databricks-115/IPText.json")

  IPAddressDF.show()

  // Columns
  val firstColumn = IPAddressDF.col("IPAddress")
}
