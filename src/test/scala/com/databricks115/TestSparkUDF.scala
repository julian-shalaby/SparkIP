package com.databricks115
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.FunSuite
import org.apache.spark.sql.functions.udf

/*
  ToDo:
    1) Make a hashmap for IPSets that will appended/remove sets when they are created/deleted
    2) Put all of the UDFs and data structures (IPSet hashmap) into a class or object so it's exportable
    3) Anything else that cleans the interface up, improves efficiency, or adds necessary functionality
 */

class TestSparkUDF extends FunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  val path = "src/test/scala/com/databricks115/ipMixedFile.json"
  val ipDF: DataFrame = spark.read.json(path)
  ipDF.createOrReplaceTempView("IPAddresses")

  test("Network contains") {
    //function and function registration to check if the IP address is in the IP network
    spark.udf.register("netContains", udf((ip: String, net: String) => IPNetwork(net).contains(IPAddress(ip))))

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
    //check if an ip is multicast
    spark.udf.register("isMulticast", udf((IPAddr: String) => IPAddress(IPAddr).isMulticast))

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE isMulticast(IPAddress)"""
      )
    )

  }

  test("IPSet") {
    val ipset = IPSet("192.0.0.0", "::", "2001::", "::2001", "2.0.4.3", "208.129.250.9", "::/8", "192.0.0.0/8")
    val ipset2 = IPSet("7.0.0.0", "::", "8::", "::9", "2.8.4.3")
    val ipMap: Map[String, IPSet] = Map("ipset" -> ipset, "ipset2" -> ipset2)

    spark.udf.register("setContains", udf((IPAddr: String, set: String) => ipMap(set) contains IPAddr))

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE setContains(IPAddress, "ipset")"""
      ).show
    )
  }

}
