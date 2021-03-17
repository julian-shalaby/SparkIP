package com.databricks115
import java.math.BigInteger
import scala.math.BigInt.javaBigInteger2bigInt
import scala.util.matching.Regex

/*
  ToDo:
    1) Could potentially redo the addr, cidr parsing without regex to make it more efficient. Will be really hard to
    match every case (and only those cases) without regex though
 */

case class IPv6Network (ipaddress: String) extends IPv6Traits {
  // for if input is in range format
  private var IP2: Option[String] = None

  private val (addr: String, cidr: Int) = {
    //regex
    val IPv6NetworkCIDR: Regex = """(([0-9a-f]{1,4}:){7,7}[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}|[0-9a-f]{1,4}:((:[0-9a-f]{1,4}){1,6})|:((:[0-9a-f]{1,4}){1,7}|:)|fe80:(:[0-9a-f]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-f]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))\/(\d{1,3})""".r
    lazy val IPv6Address: Regex = """([0-9a-f]{1,4}:){7,7}[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}|[0-9a-f]{1,4}:((:[0-9a-f]{1,4}){1,6})|:((:[0-9a-f]{1,4}){1,7}|:)|fe80:(:[0-9a-f]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-f]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])""".r
    lazy val IPv6NetworkRange: Regex = """(([0-9a-f]{1,4}:){7,7}[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}|[0-9a-f]{1,4}:((:[0-9a-f]{1,4}){1,6})|:((:[0-9a-f]{1,4}){1,7}|:)|fe80:(:[0-9a-f]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-f]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))\-(([0-9a-f]{1,4}:){7,7}[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}|[0-9a-f]{1,4}:((:[0-9a-f]{1,4}){1,6})|:((:[0-9a-f]{1,4}){1,7}|:)|fe80:(:[0-9a-f]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-f]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))""".r
    ipaddress match {
      case IPv6NetworkCIDR(_*) =>
        val Array(addrString, cidrBlock) = ipaddress.split('/')
        require(cidrBlock.toInt >= 0 && cidrBlock.toInt <= 128, "Bad IPv6 Network CIDR.")
        require(isNetworkAddressInternal(addrString, cidrBlock.toInt), "IP address must be the network address.")
        (addrString, cidrBlock.toInt)

      case IPv6Address(_*) => (ipaddress, 128)

      case IPv6NetworkRange(_*) =>
        val Array(addrString1, addrString2) = ipaddress.split('-')
        IP2 = Some(addrString2)
        (addrString1, -1)

      case _ => throw new Exception("Bad IPv6 Network Format.")
    }
  }

  // start and end of the network
  private val (addrBIStart: BigInteger, addrBIEnd: BigInteger) = {
    val addrBI = IPv6ToBigInteger(addr)
    (
      if (IP2.isDefined) IPv6ToBigInteger(addr)
      else new BigInteger("340282366920938463463374607431768211455")
        .shiftLeft(new BigInteger("128").subtract(new BigInteger(s"$cidr")).toInt)
        .and(IPv6ToBigInteger(addr)),

      if (IP2.isDefined) IPv6ToBigInteger(IP2.getOrElse(throw new Exception("Bad IPv6 Network Range.")))
      else addrBI.or(
          new BigInteger("1")
            .shiftLeft(128 - cidr)
            .subtract(new BigInteger("1")))
    )

  }

  // range of the network
  lazy val range: String = s"${bigIntegerToIPv6(addrBIStart)}-${bigIntegerToIPv6(addrBIEnd)}"

  // access operators
  lazy val networkAddress: IPv6 = bigIntegerToIPv6(addrBIStart)
  lazy val broadcastAddress: IPv6 = bigIntegerToIPv6(addrBIEnd)

  // compare networks
  def ==(that: IPv6Network): Boolean = this.addrBIStart == that.addrBIStart && this.addrBIEnd == that.addrBIEnd
  def !=(that: IPv6Network): Boolean = this.addrBIStart != that.addrBIStart || this.addrBIEnd != that.addrBIEnd
  def <(that: IPv6Network): Boolean = {
    this.addrBIStart < that.addrBIStart ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd < that.addrBIEnd)
  }
  def >(that: IPv6Network): Boolean = {
    this.addrBIStart > that.addrBIStart ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd > that.addrBIEnd)
  }
  def <=(that: IPv6Network): Boolean = {
    this.addrBIStart < that.addrBIStart ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd < that.addrBIEnd) ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd == that.addrBIEnd)
  }
  def >=(that: IPv6Network): Boolean = {
    this.addrBIStart > that.addrBIStart ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd > that.addrBIEnd) ||
      (this.addrBIStart == that.addrBIStart && this.addrBIEnd == that.addrBIEnd)
  }

  // checks if an IP is in the network
  def netContainsIP(ip: IPv6): Boolean = if (ip.addrBI >= addrBIStart && ip.addrBI <= addrBIEnd) true else false

  // checks if networks overlap
  def netsIntersect(net: IPv6Network): Boolean = if (this.addrBIStart <= net.addrBIEnd && this.addrBIEnd >= net.addrBIStart) true else false

  // checks whether a ip address is the network address of this network
  def isNetworkAddress(addrStr: String): Boolean = isNetworkAddressInternal(addrStr, cidr)
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip: IPv6 = IPv6(addrStr)
    val netAddr: IPv6 = ip.mask(cidrBlock)
    ip == netAddr
  }

}

object IPv6Network {
  def apply(addr: IPv6) = new IPv6Network(addr.ipaddress)
}
