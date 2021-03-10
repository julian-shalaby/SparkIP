package com.databricks115
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

/*
  ToDo:
    Make UDAFs for stuff like GROUPBY
    Make functions for all IP address types
    Figure out how to use objects as parameters for UDFs
    Allow IPNetContains (and possibly other functions) to accept multiple parameter type inputs
 */

object Main extends App {
  val spark = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  //load the json and create a view to query
  val path = "src/main/scala/com/databricks-115/IPText.json"
  val IPv4DF = spark.read.json(path)
  IPv4DF.createOrReplaceTempView("IPv4")

  //function and function registration to check if the IP address is in the IP network
  val network1 = IPNetwork("192.0.0.0/17")
  val IPNetContains = udf((IPAddr: IPv4) => network1.netContainsIP(IPAddr))
  spark.udf.register("IPNetContains", IPNetContains)
  //query to test the function
  val t0 = System.nanoTime()
  spark.sql(
    """SELECT *
    FROM IPv4
    WHERE IPNetContains(IPAddress)"""
  ).show()
  val t1 = System.nanoTime()
  val elapsed = t1 - t0
  println(s"Elapsed time: $elapsed ns")

  val t2 = System.nanoTime()
  spark.sql(
    """SELECT *
    FROM IPv4
    WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
  ).show()
  val t3 = System.nanoTime()
  val elapsed2 = t3 - t2
  println(s"Elapsed time: $elapsed2 ns")

//  //passing objects to UDFs isn't working for some reason?
//  val IPSet1 = IPSet(Seq("212.222.131.201", "212.222.131.200", "192.0.0.0/16"))
//  //function and function registration to check if the IP address is in the IP Set
//  val IPSetContains = udf((IPAddr: String, IPSetObj: IPSet) => IPSetObj.contains(IPv4(IPAddr)))
//  spark.udf.register("IPSetContains", IPSetContains)
//  //query to test the function
//  spark.sql(
//    "SELECT * " +
//      "FROM IPv4 " +
//      s"WHERE IPSetContains(IPAddress, $IPSet1)"
//  ).show()

  //function and function registration to check if the IP address is a multicast one
//  val IPIsMulticast = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
//  spark.udf.register("IPIsMulticast", IPIsMulticast)
//  //query to test the function
//  spark.sql(
//    """SELECT *
//      FROM IPv4
//      WHERE IPIsMulticast(IPAddress)"""
//  ).show()

}