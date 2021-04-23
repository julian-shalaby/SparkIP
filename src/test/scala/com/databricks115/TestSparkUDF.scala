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
  val IPv4DS: Dataset[IPAddress] = spark.read.json(path).as[IPAddress]
  val IPv4DSSmall: Dataset[IPAddress] = spark.read.json(path2).as[IPAddress]

  // IPv6
  val path3 = "src/test/scala/com/databricks115/ipv6File.json"
  val IPv6DF: DataFrame = spark.read.json(path3)
  IPv6DF.createOrReplaceTempView("IPv6")
  val IPv6DS: Dataset[IPAddress] = spark.read.json(path3).as[IPAddress]

  // Mixed
  val path4 = "src/test/scala/com/databricks115/ipMixedFile.json"
  val IPDF: DataFrame = spark.read.json(path4)
  IPDF.createOrReplaceTempView("IPs")
  val IPDS: Dataset[IPAddress] = spark.read.json(path4).as[IPAddress]

  test("IPNetwork contains /17") {
    //function and function registration to check if the IP address is in the IP network
    val network1: IPv4Network = IPv4Network("192.0.0.0/17")
    val IPNetContains: UserDefinedFunction = udf((IPAddr: String) => network1.contains(IPAddress(IPAddr)))
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
    val IPIsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPAddress(IPAddr).isMulticast)
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

  test("IPv6") {
    //check if an ip is multicast
    val IPv6IsMulticast: UserDefinedFunction = udf((IPAddr: String) => IPAddress(IPAddr).isMulticast)
    spark.udf.register("IPv6IsMulticast", IPv6IsMulticast)

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPv6
        WHERE IPv6IsMulticast(IPAddress)"""
      ).show
    )

    //using dataset filter
    spark.time(
      IPv6DS.filter(ip => ip.isMulticast).show
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
      IPv4DS.filter(ip => ip.isMulticast).rdd.sortBy(i => i.addrBI).toDS.show()
    )
  }

  test("IP mixed") {
    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPs"""
      ).show
    )

    //using dataset filter
    spark.time(
      IPDS.show
    )
  }

  test("IPSet") {
    val ip = IPAddress("1::")
    val ipset = IPSet("192.0.0.0", "::", "2001::", "::2001", "2.0.4.3", "208.129.250.9", "efc6:bf54:b54b:80b7:8190:6b8b:6ca2:a3f9")
    ipset.add(ip)
    val inSet: UserDefinedFunction = udf((IPAddr: String) => ipset contains IPAddr)
    spark.udf.register("inSet", inSet)

    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPs
        WHERE inSet(IPAddress)"""
      ).show
    )

    //using dataset filter
    spark.time(
      IPDS.filter(ip => ipset.contains(ip)).show
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
