package com.databricks115
import scala.util.matching.Regex

case class IPv4Network(ipaddress: String) extends IPv4Traits {
  // for if input is in range format
  private var IP2: Option[String] = None

  // parse IPv4 and subnet
  private val (addr: String, cidr: Int) = {
    //1.1.1.1/16 format
    val NetworkCIDR: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/(\d{1,2})""".r
    //1.1.1.1/255.255.0.0 format
    lazy val NetworkDottedDecimal: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    //ipv4 address
    lazy val IPv4Address: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    //1.1.1.1-2.2.2.2 format
    lazy val NetworkIPRange: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\-([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    //Address 1.1.1.1 Netmask 255.255.255.0 format
    lazy val NetworkVerboseDottedDecimal: Regex = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r

    def IPv4subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')

    ipaddress match {
      case NetworkCIDR(o1, o2, o3, o4, o5) =>
        require(o5.toInt >= 0 && o5.toInt <= 32, "Bad IPv4 Network CIDR.")
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrBlock = o5.toInt
        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
        (addrStr, cidrBlock)

      case NetworkDottedDecimal(o1, o2, o3, o4, o5, o6, o7, o8) =>
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrString = s"$o5.$o6.$o7.$o8"
        val cidrBlock = IPv4subnetToCidr(s"$o5.$o6.$o7.$o8")
        require(isNetworkAddressInternal(cidrString, cidrBlock), "Dotted decimal IP address must be the network address.")
        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
        (addrStr, cidrBlock)

      case IPv4Address(o1, o2, o3, o4) => (s"$o1.$o2.$o3.$o4", 32)

      case NetworkIPRange(o1, o2, o3, o4, o5, o6, o7, o8) =>
        IP2 = Some(s"$o5.$o6.$o7.$o8")
        (s"$o1.$o2.$o3.$o4", -1)

      case NetworkVerboseDottedDecimal(s1, o1, o2, o3, o4, s2, o5, o6, o7, o8) =>
        val addrStr = s"$o1.$o2.$o3.$o4"
        val cidrString = s"$o5.$o6.$o7.$o8"
        val cidrBlock = IPv4subnetToCidr(s"$o5.$o6.$o7.$o8")
        require(isNetworkAddressInternal(cidrString,cidrBlock), "Verbose dotted decimal IP address must be the network address.")
        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
        (s"$o1.$o2.$o3.$o4", IPv4subnetToCidr(s"$o5.$o6.$o7.$o8"))

      case _ => throw new Exception("Bad IPv4 Network Format.")
    }
  }

  // start and end of the network
  private val (addrLStart: Long, addrLEnd: Long) = {
    val addrL = IPv4ToLong(addr)
    (if (IP2.isDefined) addrL else 0xFFFFFFFF << (32 - cidr) & addrL,
      if (IP2.isDefined) IPv4ToLong(IP2.getOrElse(throw new Exception ("Bad IPv4 Network Range."))) else addrL | ((1L << (32 - cidr)) - 1))
  }

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
  def isNetworkAddress(addrStr: String): Boolean = isNetworkAddressInternal(addrStr, cidr)
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip: IPv4 = IPv4(addrStr)
    val netAddr: IPv4 = ip.mask(cidrBlock)
    ip == netAddr
  }
}

object IPv4Network {
  def apply(addrStr: IPv4) = new IPv4Network(addrStr.ipaddress)
}
