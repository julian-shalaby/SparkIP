package com.databricks115
import org.apache.spark.sql.expressions.UserDefinedFunction
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
  val path2 = "src/test/scala/com/databricks-115/ipFileSmall.json"

  //1 mil addresses
  val IPv4DF: DataFrame = spark.read.json(path)
  //10k addresses
  val IPv4DFSmall: DataFrame = spark.read.json(path2)

  IPv4DF.createOrReplaceTempView("IPv4")
  IPv4DFSmall.createOrReplaceTempView("IPv4Small")

  def timeFunc(funcToTime: () => Unit) {
    val t0 = System.nanoTime()
    funcToTime()
    val t1 = System.nanoTime()
    val elapsed = t1 - t0
    println(s"Elapsed time: $elapsed ns or ${elapsed/1000000000} sec")
  }

  //UDFs
  //function and function registration to check if the IP address is in the IP network
  val IPNetContains: UserDefinedFunction = udf((IPNet: String, IPAddr: String) => IPv4Network(IPNet).netContainsIP(IPv4(IPAddr)))
  spark.udf.register("IPNetContains", IPNetContains)
  //check if an ip is multicast
  val IPIsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
  spark.udf.register("IPIsMulticast", IPIsMulticast)

  test("IPNetwork contains /17") {
    //using func
    timeFunc(() =>
      spark.sql(
      s"""SELECT *
         FROM IPv4Small
         WHERE IPNetContains("192.0.0.0/17", IPAddress)"""
    ).show())
    //using regex
    timeFunc(() =>
      spark.sql(
      """SELECT *
         FROM IPv4Small
         WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
    ).show())
  }
  test("IPNetwork contains /17 1 conversion") {
    //function and function registration to check if the IP address is in the IP network
    val IPNet = IPv4Network("192.0.0.0/17")
    val IPNetContains1Conversion = udf((IPAddr: String) => IPNet.netContainsIP(IPv4(IPAddr)))
    spark.udf.register("IPNetContains1Conversion", IPNetContains1Conversion)

    //using func
    timeFunc(() =>
      spark.sql(
        s"""SELECT *
         FROM IPv4
         WHERE IPNetContains1Conversion(IPAddress)"""
      ).show())
    //using regex
    timeFunc(() =>
      spark.sql(
        """SELECT *
         FROM IPv4
         WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
      ).show())
  }
  test("IPNetwork contains /17 no conversion") {
    val IPNet = IPv4Network("192.0.0.0/17")
    val IPAddr = IPv4("192.0.78.70")
    val IPNetContainsNoConversion = udf(() => IPNet.netContainsIP(IPAddr))
    spark.udf.register("IPNetContainsNoConversion", IPNetContainsNoConversion)

    //using func
    timeFunc(() =>
      spark.sql(
        s"""SELECT *
         FROM IPv4
         WHERE IPNetContainsNoConversion()"""
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

}
