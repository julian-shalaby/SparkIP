package com.databricks115

import scala.util.matching.Regex

case class IPv4Network(addr: String) extends sharedIPTraits with IPv4Traits {
  // for if input is in range format
  private var IP2: Option[String] = None

  // parse IPv4 and subnet
  private val parsedAddr: (String, Int) = {
    //ip address = 4 octets
    //network cidr = 5 octets
    //network dotted = 8 octets
    //network range = 8 octets
    //verbose = 8 but could be taken out maybe

    //ipv4 address
    val IPv4Address: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    //1.1.1.1/16 format
    val NetworkCIDR: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
    //1.1.1.1/255.255.0.0 format
    val NetworkDottedDecimal: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    //Address 1.1.1.1 Netmask 255.255.255.0 format
    val NetworkVerboseDottedDecimal: Regex = """(^Address )(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})( Netmask )(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    //1.1.1.1-2.2.2.2 format
    val NetworkIPRange: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\-(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    addr match {
      case IPv4Address(o1, o2, o3, o4) =>
        require(IPv4Validation(List(o1, o2, o3, o4)), "Network is invalid")
        (s"$o1.$o2.$o3.$o4", 32)

      case NetworkCIDR(o1, o2, o3, o4, o5) =>
        require(IPv4Validation(List(o1, o2, o3, o4)) && o5.toInt >= 0 && o5.toInt <= 32, "Network is invalid")
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrBlock = o5.toInt
        require(isNetworkAddressInternal(addrStr, cidrBlock), "CIDR ip address must be the network address")
        (addrStr, cidrBlock)

      case NetworkDottedDecimal(o1, o2, o3, o4, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid")
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrString = s"$o5.$o6.$o7.$o8"
        val cidrBlock = IPv4subnetToCidr(s"$o5.$o6.$o7.$o8")
        require(isNetworkAddressInternal(cidrString,cidrBlock), "Dotted decimal ip address must be the network address")
        require(isNetworkAddressInternal(addrStr, cidrBlock), "ip address must be the network address")
        (addrStr, cidrBlock)

      case NetworkVerboseDottedDecimal(s1, o1, o2, o3, o4, s2, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid")
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrString = s"$o5.$o6.$o7.$o8"
        val cidrBlock = IPv4subnetToCidr(s"$o5.$o6.$o7.$o8")
        require(isNetworkAddressInternal(cidrString,cidrBlock), "Verbose dotted decimal ip address must be the network address")
        require(isNetworkAddressInternal(addrStr, cidrBlock), "ip address must be the network address")
        (s"$o1.$o2.$o3.$o4", IPv4subnetToCidr(s"$o5.$o6.$o7.$o8"))

      case NetworkIPRange(o1, o2, o3, o4, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid")
        IP2 = Some(s"$o5.$o6.$o7.$o8")
        (s"$o1.$o2.$o3.$o4", -1)

      case _ => throw new Exception
    }
  }

  // start and end of the network
  private val addrLStart: Long =
    if (IP2.isDefined) IPv4ToLong(parsedAddr._1) else
    0xFFFFFFFF << (32 - parsedAddr._2) & IPv4ToLong(parsedAddr._1)

  private val addrLEnd: Long =
    if (IP2.isDefined) IPv4ToLong(IP2.getOrElse(throw new Exception)) else
      addrLStart + math.pow(2, 32-parsedAddr._2).toLong - 1
  // range of the network
  lazy val range: String = s"${longToIPv4(addrLStart)}-${longToIPv4(addrLEnd)}"

  // access operators
  lazy val networkAddress: IPv4 = longToIPv4(addrLStart)
  lazy val broadcastAddress: IPv4 = longToIPv4(addrLEnd)

  // compare networks
  def ==(that: IPv4Network): Boolean = this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd
  def !=(that: IPv4Network): Boolean = this.addrLStart != that.addrLStart || this.addrLEnd != that.addrLEnd
  def <(that: IPv4Network): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd)
  }
  def >(that: IPv4Network): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd)
  }
  def <=(that: IPv4Network): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }
  def >=(that: IPv4Network): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }

  // checks if an IP is in the network
  def netContainsIP(ip: IPv4): Boolean = if (ip.addrL >= addrLStart && ip.addrL <= addrLEnd) true else false
  
  // checks if networks overlap
  def netsIntersect(net: IPv4Network): Boolean = if (this.addrLStart <= net.addrLEnd && this.addrLEnd >= net.addrLStart) true else false
  
  // checks whether a ip address is the network address of this network
  def isNetworkAddress(addr: String): Boolean = isNetworkAddressInternal(addr, parsedAddr._2)
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip: IPv4 = IPv4(addrStr)
    val netAddr: IPv4 = ip.mask(cidrBlock)
    ip == netAddr
  }
}

object IPv4Network {
  def apply(addr: IPv4) = new IPv4Network(addr.IPAddress)
}
