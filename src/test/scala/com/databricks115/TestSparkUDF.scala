package com.databricks115
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.FunSuite
import com.databricks115.SparkIP._

class TestSparkUDF extends FunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  val path = "src/test/scala/com/databricks115/ipMixedFile.json"
  val ipDF: DataFrame = spark.read.json(path)
  ipDF.createOrReplaceTempView("IPAddresses")

  SparkIP(spark)

  test("Network contains") {
    //using func
      spark.time(
        spark.sql(
        """SELECT *
         FROM IPAddresses
         WHERE netContains(IPAddress, "192.0.0.0/16")"""
        )
      )
  }

  test("isMulticast") {
    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE isMulticast(IPAddress)"""
      )
    )

    ipDF.select("*").filter(isMulticast(col("IPAddress")))
  }

  test("IPSet") {
    val ipset = IPSet("192.0.0.0", "::", "2001::", "::2001", "2.0.4.3", "208.129.250.9", "::/8", "192.0.0.0/8")
    SparkIP.add(ipset,"ipset")

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE setContains(IPAddress, "ipset")"""
      ).show
    )
    ipDF.select("*").filter(setContains(ipset)(col("IPAddress")))

  }

}
