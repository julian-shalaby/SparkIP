package com.databricks115

import org.apache.spark.sql.{DataFrame, SparkSession}

object TestSparkIP extends App {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  val path = "src/test/scala/com/databricks115/ipMixedFile.json"
  val ipDF: DataFrame = spark.read.json(path)
  ipDF.createOrReplaceTempView("IPAddresses")

  SparkIPInit(spark)

  spark.sql(
    """SELECT *
      FROM IPAddresses
      WHERE isMulticast(IPAddress)"""
  ).show()


}
