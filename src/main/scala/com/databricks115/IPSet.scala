package com.databricks115

import scala.collection.mutable.ArrayBuffer

/*
  ToDo:
    1) Add support for IPNetworks using an array buffer and linear searching
    2) Add more operations (intersect, union, etc)
    3) See if we can potentially get log (n) searching back without "Task not serializable" errors in Spark
 */

case class IPSet(ipAddresses: Any*) {
  def this() = this(null)
  var ipMap: Map[String, BigInt] = Map()
  var netArray: ArrayBuffer[Any] = ArrayBuffer()
  private def initializeMap(): Unit = {
    ipAddresses.foreach {
      case s: String => ipMap += (s -> IPAddress(s).addrBI)
      case ipAddr: IPAddress => ipMap += (ipAddr.ipAddress -> ipAddr.addrBI)
      case v4: IPv4 => ipMap += (v4.ipAddress -> v4.addrL)
      case v6: IPv6 => ipMap += (v6.ipAddress -> v6.addrBI)
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  initializeMap()
  def add(ip: Any*): Unit = {
    ip.foreach {
      case s: String => ipMap += (s -> IPAddress(s).addrBI)
      case ipAddr: IPAddress => ipMap += (ipAddr.ipAddress -> ipAddr.addrBI)
      case v4: IPv4 => ipMap += (v4.ipAddress -> v4.addrL)
      case v6: IPv6 => ipMap += (v6.ipAddress -> v6.addrBI)
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  def remove(ip: Any*): Unit = {
    ip.foreach {
      case s: String => ipMap -= s
      case ipAddr: IPAddress => ipMap -= ipAddr.ipAddress
      case v4: IPv4 => ipMap -= v4.ipAddress
      case v6: IPv4 => ipMap -= v6.ipAddress
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
  }
  def contains(ip: Any*): Boolean = {
    ip.foreach {
      case s: String => if (!ipMap.contains(s)) return false
      case ipAddr: IPAddress => if (!ipMap.contains(ipAddr.ipAddress)) return false
      case v4: IPv4 => if (!ipMap.contains(v4.ipAddress)) return false
      case v6: IPv6 => if (!ipMap.contains(v6.ipAddress)) return false
      case _ => throw new Exception("Can only accept IP Addresses or Strings.")
    }
    true
  }
}