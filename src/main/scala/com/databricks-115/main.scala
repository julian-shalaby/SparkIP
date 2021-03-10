package com.databricks115
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object main extends App {

  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._

  val path = "src/test/scala/com/databricks-115/IPText.json"
  val IPv4DS: Dataset[IPv4] = spark.read.json(path).as[IPv4]

  IPv4DS.filter(ip => ip.isMulticast).show()

}
