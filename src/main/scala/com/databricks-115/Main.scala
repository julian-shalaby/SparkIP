package com.databricks115
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
/*

  Use cases:

    SELECT *
    FROM log
    WHERE IPAddress IN '192.0.0.0/16'
    //takes the IPAddress argument into a function that runs IPNetwork's netContains function

    val IPSet1 = IPSet(Seq("212.222.131.201", "212.222.131.200", "192.0.0.0/16"))
    SELECT *
    FROM log
    WHERE IPAddress IN IPSet1
    //takes the IPAddress argument into a function that runs IPSet's contains function

    SELECT *
    FROM log
    WHERE IPAddress IS Multicast
    //takes the IPAddress argument into a function that runs IPSet's contains function

    //Would like to map inputs in the format of IP Addresses to our IPAddressType similar to Dates in SQL
    //Use UDF mixed with UDT somehow? (UDF works, but is ugly. UDT is pretty but may need UDF functionality)

*/

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
  val IPNetContains = udf((IPAddr: String, IPNet: String) => IPNetwork(IPNet).netContainsIP(IPv4(IPAddr)))
  spark.udf.register("IPNetContains", IPNetContains)
  //query to test the function
  spark.sql(
    "SELECT * " +
    "FROM IPv4 " +
    "WHERE IPNetContains(IPAddress, '192.0.0.0/24')"
  ).show()

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
  val IPIsMulticast = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
  spark.udf.register("IPIsMulticast", IPIsMulticast)
  //query to test the function
  spark.sql(
    "SELECT * " +
      "FROM IPv4 " +
      "WHERE IPIsMulticast(IPAddress)"
  ).show()

}