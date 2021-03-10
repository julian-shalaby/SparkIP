package com.databricks115
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.FunSuite
import org.apache.spark.sql.functions.udf

class TestSparkUDF extends FunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  //load the json and create a view to query
  val path = "src/test/scala/com/databricks-115/IPText.json"
  val path2 = "src/test/scala/com/databricks-115/ipv6File.json"

  val IPv4DF: DataFrame = spark.read.json(path)
  val IPv6DF: DataFrame = spark.read.json(path2)

  IPv4DF.createOrReplaceTempView("IPv4")
  IPv6DF.createOrReplaceTempView("IPv6")

  def timeFunc(funcToTime: () => Unit) {
    val t0 = System.nanoTime()
    funcToTime()
    val t1 = System.nanoTime()
    val elapsed = t1 - t0
    println(s"Elapsed time: $elapsed ns or ${elapsed/1000000000} sec")
  }

  test("IPNetwork contains /17") {
    val network1 = IPv4Network("192.0.0.0/17")

    //function and function registration to check if the IP address is in the IP network
    val IPNetContains = udf((IPAddr: String) => network1.netContainsIP(IPv4(IPAddr)))
    spark.udf.register("IPNetContains", IPNetContains)

    //using func
    timeFunc(() =>
      spark.sql(
      """SELECT *
         FROM IPv4
         WHERE IPNetContains(IPAddress)"""
    ).show())
    //using regex
    timeFunc(() =>
      spark.sql(
      """SELECT *
         FROM IPv4
         WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
    ).show())
  }

  test("IP is Multicast") {
    val IPIsMulticast = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
    spark.udf.register("IPIsMulticast", IPIsMulticast)
    //function
    timeFunc(() =>
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPIsMulticast(IPAddress)"""
      ).show()
    )

    //regex
    timeFunc(() =>
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPAddress RLIKE '(23[0-9]|22[4-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}'"""
      ).show()
    )
  }

  test("IPv6 query") {
    //regex
    timeFunc(() =>
      spark.sql(
        """SELECT *
        FROM IPv6
        """
      ).show()
    )
  }

}
