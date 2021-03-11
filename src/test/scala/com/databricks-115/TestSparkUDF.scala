package com.databricks115
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.scalatest.FunSuite
import org.apache.spark.sql.functions.udf

class TestSparkUDF extends FunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._

  //load the json and create a view to query
  val path = "src/test/scala/com/databricks-115/IPText.json"
  val path2 = "src/test/scala/com/databricks-115/ipFileSmall.json"
  //1 mil addresses
  val IPv4DF: DataFrame = spark.read.json(path)
  //10k addresses
  val IPv4DFSmall: DataFrame = spark.read.json(path2)
  IPv4DF.createOrReplaceTempView("IPv4")
  IPv4DFSmall.createOrReplaceTempView("IPv4Small")
  val IPv4DS: Dataset[IPv4] = spark.read.json(path).as[IPv4]

  //UDFs
  //function and function registration to check if the IP address is in the IP network
  val IPNetContains: UserDefinedFunction = udf((IPNet: String, IPAddr: String) => IPv4Network(IPNet).netContainsIP(IPv4(IPAddr)))
  spark.udf.register("IPNetContains", IPNetContains)
  //check if an ip is multicast
  val IPIsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
  spark.udf.register("IPIsMulticast", IPIsMulticast)

  test("IPNetwork contains /17") {

    //using func
      spark.time(
        spark.sql(
        s"""SELECT *
         FROM IPv4
         WHERE IPNetContains("192.0.0.0/17", IPAddress)"""
        ).show()
      )

    //using dataset filter
    val network1 = IPv4Network("192.0.0.0/17")
    spark.time(
      IPv4DS.filter(ip => network1.netContainsIP(ip)).show()
    )

    //using regex
    spark.time(
      spark.sql(
      """SELECT *
         FROM IPv4
         WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
      ).show()
    )

  }

  test("IP is Multicast") {

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPIsMulticast(IPAddress)"""
      ).show()
    )

    //using dataset filter
    spark.time(
      IPv4DS.filter(ip => ip.isMulticast).show()
    )

    //    //regex
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPAddress RLIKE '(23[0-9]|22[4-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}'"""
      ).show()
    )

  }

  test("Sort IP dataset") {
    //sorting multicast IPs
    spark.time(
      IPv4DS.filter(ip => ip.isMulticast).rdd.sortBy(i => i.addrL).toDS.show()
    )
  }

}
