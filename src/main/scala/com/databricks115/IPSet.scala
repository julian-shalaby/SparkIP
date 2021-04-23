package com.databricks115

case class IPSet(ipAddresses: Any*) {
  def this() = this(null)
  var ipMap: Map[String, BigInt] = Map()
  private def initializeMap(): Unit = {
    ipAddresses.foreach {
      case s: String => ipMap += (s -> IPAddress(s).addrBI)
      case ipAddr: IPAddress => ipMap += (ipAddr.ipAddress -> ipAddr.addrBI)
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  initializeMap()
  def add(ip: Any*): Unit = {
    ip.foreach {
      case s: String => ipMap += (s -> IPAddress(s).addrBI)
      case ipAddr: IPAddress => ipMap += (ipAddr.ipAddress -> ipAddr.addrBI)
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  def remove(ip: Any*): Unit = {
    ip.foreach {
      case s: String => ipMap -= s
      case ipAddr: IPAddress => ipMap -= ipAddr.ipAddress
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  def contains(ip: Any*): Boolean = {
    ip.foreach {
      case s: String => if (!ipMap.contains(s)) return false
      case ipAddr: IPAddress => if (!ipMap.contains(ipAddr.ipAddress)) return false
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
    true
  }
}