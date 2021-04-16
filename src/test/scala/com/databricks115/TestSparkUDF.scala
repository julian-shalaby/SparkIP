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
  val path = "src/test/scala/com/databricks115/IPText.json"
  val path2 = "src/test/scala/com/databricks115/ipFileSmall.json"
  //1 mil addresses
  val IPv4DF: DataFrame = spark.read.json(path)
  //10k addresses
  val IPv4DFSmall: DataFrame = spark.read.json(path2)
  IPv4DF.createOrReplaceTempView("IPv4")
  IPv4DFSmall.createOrReplaceTempView("IPv4Small")
  val IPv4DS: Dataset[IPv4] = spark.read.json(path).as[IPv4]
  val IPv4DSSmall: Dataset[IPv4] = spark.read.json(path2).as[IPv4]

  test("IPNetwork contains /17") {
    //function and function registration to check if the IP address is in the IP network
    val network1: IPv4Network = IPv4Network("192.0.0.0/17")
    val IPNetContains: UserDefinedFunction = udf((IPAddr: String) => network1.contains(IPv4(IPAddr)))
    spark.udf.register("IPNetContains", IPNetContains)

    //using regex
    spark.time(
      spark.sql(
        """SELECT *
         FROM IPv4
         WHERE IPAddress RLIKE '^192\.0\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\.[0-9]+$'"""
      )
    )

    //using func
      spark.time(
        spark.sql(
        """SELECT *
         FROM IPv4
         WHERE IPNetContains(IPAddress)"""
        )
      )

    //using dataset filter
    spark.time(
      IPv4DS.filter(ip => network1.contains(ip))
    )

  }

  test("IP is Multicast") {
    //check if an ip is multicast
    val IPIsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPv4(IPAddr).isMulticast)
    spark.udf.register("IPIsMulticast", IPIsMulticast)

    //regex
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPAddress RLIKE '(23[0-9]|22[4-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}'"""
      )
    )

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPIsMulticast(IPAddress)"""
      )
    )

    //using dataset filter
    spark.time(
      IPv4DS.filter(ip => ip.isMulticast)
    )
  }

  test("Sort IPs") {
    //regex
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv4
        WHERE IPAddress RLIKE '(23[0-9]|22[4-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}'"""
      ).sort().show()
    )

    //sorting multicast IPs
    spark.time(
      IPv4DS.filter(ip => ip.isMulticast).rdd.sortBy(i => i.addrL).toDS.show()
    )
  }
  test("Match exact IP Address")
  {
    spark.time(spark.sql(
      "SELECT * FROM IPv4 WHERE IPAddress = '192.0.2.1'"
    )).show()
  }
  test("Match within /24 network")
  {
    spark.time(spark.sql(
      "SELECT * FROM IPv4 WHERE IPAddress LIKE '192.0.2.%'"
    )).show()
  }
  test("Match within /22 network")
  {
    spark.time(spark.sql(
      "SELECT * FROM IPv4 WHERE IPAddress RLIKE '^192\\.0\\.[0-3]\\.[0-9]+$'"
    )).show()
  }
  test("Match within /17 network")
  {
    spark.time(spark.sql(
      "SELECT * FROM IPv4 WHERE IPAddress RLIKE '^192\\.0\\.([0-9]|[0-9][0-9]|1[0-1][0-9]|12[0-7])\\.[0-9]+$'"
    )).show()
  }

}
