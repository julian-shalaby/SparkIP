package com.databricks115
import java.math.BigInteger
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6Network (ipNetwork: String) extends IPv6Traits {
  // If input is in range format
  private var IP2: Option[String] = None

  // Parse the network
  private val (addr: String, cidr: Int) = {
    // ::/32 format
    val cidrSplit = ipNetwork.split('/')
    // ::-2001:: format
    lazy val rangeSplit = ipNetwork.split('-')

    if (cidrSplit.length == 2) {
      val cidr = cidrSplit(1).toInt
      require(isNetworkAddressInternal(cidrSplit(0), cidr), "IP address must be the network address.")
      require(cidr >= 0 && cidr <= 128, "Bad IPv6 Network CIDR.")
      (cidrSplit(0), cidr)
    }
    else if (rangeSplit.length == 2) {
      IP2 = Some(rangeSplit(1))
      (rangeSplit(0), -1)
    }
    // If it's an IPv6 address
    else (ipNetwork, 128)
  }

  // Start and end of the network
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

  // Range of the network
  lazy val range: String = s"${bigIntegerToIPv6(addrBIStart)}-${bigIntegerToIPv6(addrBIEnd)}"

  // Access operators
  lazy val networkAddress: IPv6 = bigIntegerToIPv6(addrBIStart)
  lazy val broadcastAddress: IPv6 = bigIntegerToIPv6(addrBIEnd)

  // Compare networks
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

  // Checks if an IP is in the network
  def contains(ip: IPv6): Boolean = ip.addrBI >= addrBIStart && ip.addrBI <= addrBIEnd

  // Checks if networks overlap
  def netsIntersect(net: IPv6Network): Boolean = this.addrBIStart <= net.addrBIEnd && this.addrBIEnd >= net.addrBIStart

  // Checks whether a IP address is the network address of this network
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip = IPv6(addrStr)
    val netAddr = ip.mask(cidrBlock)
    ip == netAddr
  }

}

object IPv6Network {
  def apply(addr: IPv6): IPv6Network = IPv6Network(s"${addr.ipAddress}/32")
}
