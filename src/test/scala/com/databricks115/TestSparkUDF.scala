package com.databricks115
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.FunSuite
import com.databricks115.SparkIP._
import org.apache.spark.sql.types.{StringType, StructField, StructType}

class TestSparkUDF extends FunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("IPAddress DataType")
    .config("spark.master", "local")
    .getOrCreate()

  val schema: StructType = StructType(Array(StructField("IPAddress", StringType, nullable = false)))
  val path = "src/test/scala/com/databricks115/ipMixedFile.json"
  val ipDF: DataFrame = spark.read.schema(schema).json(path)
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

    val net1 = IPNetwork("192.0.0.0/16")
    val net2 = "192.0.0.0/16"
    ipDF.select("*").filter(netContains(net1)(col("IPAddress")))
    ipDF.select("*").filter(netContains(net2)(col("IPAddress")))
  }

  test("isMulticast") {
    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        WHERE isMulticast(IPAddress)"""
      )
    ).show()

    ipDF.select("*").filter(isMulticast(col("IPAddress"))).show()
  }

  test("IPSet") {
    val ipset = IPSet(multicastIPs, privateIPS)
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

  test("ipAsBinary") {
    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        SORT BY ipAsBinary(IPAddress) DESC"""
      )
    ).show()

    ipDF.select("*").sort(ipAsBinary(col("IPAddress"))).show()
  }

  test("ipv4AsNum") {
    //function
    spark.time(
      spark.sql(
        """SELECT *
        FROM IPAddresses
        SORT BY ipV4AsNum(IPAddress)"""
      )
    ).show()

    ipDF.select("*").sort(ipV4AsNum(col("IPAddress"))).show()
  }

}
