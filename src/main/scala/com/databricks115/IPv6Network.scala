package com.databricks115
import java.math.BigInteger
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6Network (ipNetwork: String) extends IPv6Traits {
  // for if input is in range format
  private var IP2: Option[String] = None

  private val (addr: String, cidr: Int) = {
    val cidrSplit = ipNetwork.split('/')
    lazy val rangeSplit = ipNetwork.split('-')

    if (cidrSplit.length == 2) (cidrSplit(0), cidrSplit(1).toInt)
    else if (rangeSplit.length == 2) {
      IP2 = Some(rangeSplit(1))
      (rangeSplit(0), -1)
    }
    else (ipNetwork, 128)
  }

  // start and end of the network
  val (addrBIStart: BigInteger, addrBIEnd: BigInteger) = {
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
  def contains(ip: IPv6): Boolean = ip.addrBI >= addrBIStart && ip.addrBI <= addrBIEnd

  // checks if networks overlap
  def netsIntersect(net: IPv6Network): Boolean = this.addrBIStart <= net.addrBIEnd && this.addrBIEnd >= net.addrBIStart

  // checks whether a ip address is the network address of this network
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip = IPv6(addrStr)
    val netAddr = ip.mask(cidrBlock)
    ip == netAddr
  }

}

object IPv6Network {
  def apply(addr: IPv6): IPv6Network = IPv6Network(s"${addr.ipAddress}/32")
}
