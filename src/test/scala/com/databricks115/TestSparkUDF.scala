package com.databricks115
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
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
  import spark.implicits._

  val path = "src/test/scala/com/databricks115/ipMixedFile.json"
  val ipDF: DataFrame = spark.read.json(path)
  ipDF.createOrReplaceTempView("IPAddresses")
  val ipDS: Dataset[IPAddress] = spark.read.json(path).as[IPAddress]

  test("IPNetwork contains /17") {
    //function and function registration to check if the IP address is in the IP network
    val network1: IPNetwork = IPNetwork("192.0.0.0/17")
    val IPNetContains: UserDefinedFunction = udf((IPAddr: String) => network1.contains(IPAddress(IPAddr)))
    spark.udf.register("IPNetContains", IPNetContains)

    //using func
      spark.time(
        spark.sql(
        """SELECT *
         FROM IPAddresses
         WHERE IPNetContains(IPAddress)"""
        )
      )

    //using dataset filter
    spark.time(
      ipDS.filter(ip => network1.contains(ip))
    )

  }

  test("IP is Multicast") {
    //check if an ip is multicast
    val IPIsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPAddress(IPAddr).isMulticast)
    spark.udf.register("IPIsMulticast", IPIsMulticast)

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE IPIsMulticast(IPAddress)"""
      )
    )

    //using dataset filter
    spark.time(
      ipDS.filter(ip => ip.isMulticast)
    )
  }

  test("IPSet") {
    val ipset = IPSet("192.0.0.0", "::", "2001::", "::2001", "2.0.4.3", "208.129.250.9", "efc6:bf54:b54b:80b7:8190:6b8b:6ca2:a3f9")
    val ipset2 = IPSet("7.0.0.0", "::", "8::", "::9", "2.8.4.3")
    val ipMap: Map[String, IPSet] = Map("ipset" -> ipset, "ipset2" -> ipset2)
    val setContains: UserDefinedFunction = udf((IPAddr: String, set: String) => ipMap(set) contains IPAddr)
    spark.udf.register("setContains", setContains)

    //function
    spark.time(
      spark.sql(
        """SELECT IPAddress
        FROM IPAddresses
        WHERE setContains(IPAddress, 'ipset2')"""
      ).show
    )

    //using dataset filter
    spark.time(
      ipDS.filter(ip => ipset.contains(ip)).show
    )
  }

}
