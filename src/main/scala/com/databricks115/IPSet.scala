package com.databricks115

case class IPSet2(ipAddresses: IPAddress*) {
  def this() = this(null)
  var ipMap: Map[IPAddress, BigInt] = Map()
  private def initializeMap(): Unit = ipAddresses.foreach(ip => ipMap += (ip -> ip.addrBI))
  initializeMap()
  def add(ip: IPAddress): Unit = ipMap += (ip -> ip.addrBI)
  def remove(ip: IPAddress): Unit = ipMap -= ip
  def contains(ip: IPAddress): Boolean = if(ipMap.contains(ip)) true else false
}

case class IPSet(ipAddresses: String*) {
  def this() = this(null)
  var ipMap: Map[String, BigInt] = Map()
  private def initializeMap(): Unit = ipAddresses.foreach(ip => ipMap += (ip -> IPAddress(ip).addrBI))
  initializeMap()
  def add(ip: String): Unit = ipMap += (ip -> IPAddress(ip).addrBI)
  def remove(ip: String): Unit = ipMap -= ip
  def contains(ip: String): Boolean = if(ipMap.contains(ip)) true else false
}