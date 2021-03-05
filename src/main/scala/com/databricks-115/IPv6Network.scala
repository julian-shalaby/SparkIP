package com.databricks115
import java.math.BigInteger
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6Network (addr: String) extends IPType with IPv6Conversions with IPv6Regex {
  // for if input is in range format
  private var IP2: Option[String] = None

  private def parseNetwork: (String, Int) = addr match {
    case IPv6Address(_*) => (addr, 128)
    case IPv6NetworkCIDR(_*) =>
      val Array(addrString, cidrBlock) = addr.split("/")
      require(cidrBlock.toInt >= 1 && cidrBlock.toInt <= 128)
      (addrString, cidrBlock.toInt)
    case IPv6NetworkRange(_*) =>
      val Array(addrString1, addrString2) = addr.split("-")
      IP2 = Some(addrString2)
      (addrString1, -1)
    case _ => throw new Exception
    }
  private val parsedAddr: (String, Int) = parseNetwork

  val addrBIStart: BigInteger = if (IP2.isDefined) IPv6ToBigInteger(parsedAddr._1)
  else new BigInteger("340282366920938463463374607431768211455")
    .shiftLeft(new BigInteger("128").subtract(new BigInteger(s"${parsedAddr._2}")).toInt)
    .and(IPv6ToBigInteger(parsedAddr._1))

  val addrBIEnd: BigInteger = if (IP2.isDefined) IPv6ToBigInteger(IP2.getOrElse(throw new Exception))
  else addrBIStart
    .add(new BigInteger("2").pow(128-parsedAddr._2))
    .subtract(new BigInteger("1"))

  // range of the network
  val range: String = s"${bigIntegerToIPv6(addrBIStart)}-${bigIntegerToIPv6(addrBIEnd)}"

  // access operators
  lazy val networkAddress: IPv6 = IPv6(bigIntegerToIPv6(addrBIStart))
  lazy val broadcastAddress: IPv6 = IPv6(bigIntegerToIPv6(addrBIEnd))

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
  def isNetworkAddress(addr: String): Boolean = isNetworkAddressInternal(addr, parsedAddr._2)
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip: IPv6 = IPv6(addrStr)
    val netAddr: IPv6 = ip.mask(cidrBlock)
    ip == netAddr
  }

}

object IPv6Network {
  def apply(addr: IPv6) = new IPv6Network(addr.addr)
}
