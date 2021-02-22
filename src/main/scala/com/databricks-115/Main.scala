package com.databricks115
import org.apache.spark.sql.SparkSession

object Main extends App {
  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._
  val seq = Seq(
    IPNetwork("192.33.45.2/23"),
    IPNetwork("0.0.0.0/16"),
    IPNetwork("1.0.0.0/16")
  ).toDS

  //seq.show()
}