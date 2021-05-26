package com.databricks115

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

case object SparkIP {
  var spark: SparkSession = _
  var logLevel: String = _
  var setMap: scala.collection.mutable.Map[String, IPSet] = scala.collection.mutable.Map()

  // Pure UDFs
  // Multicast
  def isMulticast: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isMulticast)
  // Private
  def isPrivate: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isPrivate)
  // Global
  def isGlobal: UserDefinedFunction = udf((ip: String) => !IPAddress(ip).isPrivate)
  // Link Local
  def isLinkLocal: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isLinkLocal)
  // LoopBack
  def isLoopback: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isLoopback)
  // Unspecified
  def isUnspecified: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isUnspecified)
  // IPv4 Mapped
  def isIPv4Mapped: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isIPv4Mapped)
  // IPv4 Translated
  def isIPv4Translated: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isIPv4Translated)
  // IPv4 IPv6 Translated
  def isIPv4IPv6Translated: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isIPv4IPv6Translated)
  // Teredo
  def isTeredo: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isTeredo)
  // 6to4
  def is6to4: UserDefinedFunction = udf((ip: String) => IPAddress(ip).is6to4)
  // Reserved
  def isReserved: UserDefinedFunction = udf((ip: String) => IPAddress(ip).isReserved)
  // IPv4
  def isIPv4: UserDefinedFunction = udf((ip: String) => IPAddress(ip).version == 4)
  // IPv6
  def isIPv6: UserDefinedFunction = udf((ip: String) => IPAddress(ip).version == 6)
  // IPv4 as num
  def ipV4AsNum: UserDefinedFunction = udf((ip: String) => {
    val temp = IPAddress(ip)
    if (temp.version == 4) temp.addrNum.left.get
    else -1
  })
  // IP as binary
  def ipAsBinary: UserDefinedFunction = udf((ip: String) => {
    val temp = IPAddress(ip)
    if (temp.version == 4) "0"*128 + temp.addrNum.left.get.toBinaryString takeRight 128
    else "0"*128 + temp.addrNum.right.get.toString(2) takeRight 128
  })

  // Network Contains
  def netContains(ipnet: IPNetwork): UserDefinedFunction = udf((ip: String) => ipnet contains ip)
  def netContains(ipnet: String): UserDefinedFunction = udf((ip: String) => IPNetwork(ipnet) contains ip)
  // Set Contains
  def setContains(ipset: IPSet): UserDefinedFunction = udf((ip: String) => ipset contains ip)

  def apply(ss: SparkSession, ll: String = null): Unit = {
    spark = ss
    // Multicast
    spark.udf.register("isMulticast", isMulticast)
    // Private
    spark.udf.register("isPrivate", isPrivate)
    // Global
    spark.udf.register("isGlobal", isGlobal)
    // Link Local
    spark.udf.register("isLinkLocal", isLinkLocal)
    // LoopBack
    spark.udf.register("isLoopback", isLoopback)
    // Unspecified
    spark.udf.register("isUnspecified", isUnspecified)
    // IPv4 Mapped
    spark.udf.register("isIPv4Mapped", isIPv4Mapped)
    // IPv4 Translated
    spark.udf.register("isIPv4Translated", isIPv4Translated)
    // IPv4 IPv6 Translated
    spark.udf.register("isIPv4IPv6Translated", isIPv4IPv6Translated)
    // Teredo
    spark.udf.register("isTeredo", isTeredo)
    // 6to4
    spark.udf.register("is6to4", is6to4)
    // Reserved
    spark.udf.register("isReserved", isReserved)
    // IPv4
    spark.udf.register("isIPv4", isIPv4)
    // IPv6
    spark.udf.register("isIPv6", isIPv6)
    // IPv4 as num
    spark.udf.register("ipV4AsNum", ipV4AsNum)
    // IP as binary
    spark.udf.register("ipAsBinary", ipAsBinary)
    // Network Contains
    spark.udf.register("netContains", udf((ip: String, net: String) => IPNetwork(net).contains(IPAddress(ip))))
    if (ll == null) {
      println("No log level specified for SparkIP. Setting log level to WARN.")
      logLevel = "WARN"
    }
    else logLevel = ll
    update_sets()
  }

  def update_sets(): Unit = {
    if (spark == null) return
    spark.sparkContext.setLogLevel("FATAL")
    spark.udf.register("setContains", udf((ip: String, set: String) => setMap(set) contains ip))
    spark.sparkContext.setLogLevel(logLevel)
  }

  def add(setToAdd: IPSet, setName: String): Unit = {
    setMap += (setName -> setToAdd)
    update_sets()
  }

  def remove(setName: String*): Unit = {
    setName.foreach(setMap -= _)
    update_sets()
  }

  def clear(): Unit = setMap.clear()

  def setsAvailable(): Unit = setMap.keys.foreach(println)

  // IP Networks
  val multicastIPs = Set(IPNetwork("224.0.0.0/4"), IPNetwork("ff00::/8"))
  val privateIPS = Set(IPNetwork("0.0.0.0/8"), IPNetwork("10.0.0.0/8"), IPNetwork("127.0.0.0/8"),
    IPNetwork("169.254.0.0/16"), IPNetwork("172.16.0.0/12"), IPNetwork("192.0.0.0/29"), IPNetwork("192.0.0.170/31"),
    IPNetwork("192.0.2.0/24"), IPNetwork("192.168.0.0/16"), IPNetwork("198.18.0.0/15"), IPNetwork("198.51.100.0/24"),
    IPNetwork("203.0.113.0/24"), IPNetwork("240.0.0.0/4"), IPAddress("255.255.255.255"), IPAddress("::1"),
    IPAddress("::"), IPNetwork("::ffff:0:0/96"), IPNetwork("100::/64"), IPNetwork("2001::/23"),
    IPNetwork("2001:2::/48"), IPNetwork("2001:db8::/32"), IPNetwork("2001:10::/28"), IPNetwork("fc00::/7"))
  val publicIPs: IPNetwork = IPNetwork("100.64.0.0/10")
  val reservedIPs: IPNetwork = IPNetwork("240.0.0.0/4")
  val unspecifiedIPs = Set(IPAddress("0.0.0.0"), IPAddress("::"))
  val linkLocalIPs = Set(IPNetwork("169.254.0.0/16"), IPNetwork("fe80::/10"))
  val loopBackIPs = Set(IPNetwork("127.0.0.0/8"), IPAddress("::1"))
  val ipv4MappedIPs: IPNetwork = IPNetwork("::ffff:0:0/96")
  val ipv4TranslatedIPs: IPNetwork = IPNetwork("::ffff:0:0:0/96")
  val ipv4ipv6TranslatedIPs: IPNetwork = IPNetwork("64:ff9b::/96")
  val teredoIPs: IPNetwork = IPNetwork("2001::/32")
  val sixToFourIPs: IPNetwork = IPNetwork("2002::/16")

}
