package com.databricks115

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
      val cidrBlock = cidrSplit(1).toInt
      require(isNetworkAddressInternal(cidrSplit(0), cidrBlock), "IP address must be the network address.")
      require(cidrBlock >= 0 && cidrBlock <= 128, "Bad IPv6 Network CIDR.")
      (cidrSplit(0), cidrBlock)
    } else if (rangeSplit.length == 2) {
      IP2 = Some(rangeSplit(1))
      (rangeSplit(0), -1) 
    } else {
      // If it's an IPv6 address
      (ipNetwork, 128)
    }
  }

  // Start and end of the network
  val (addrBIStart: BigInt, addrBIEnd: BigInt) = {
    val addrBI = IPv6ToBigInt(addr)
    (if (IP2.isDefined) IPv6ToBigInt(addr) else BigInt("340282366920938463463374607431768211455") << (128-cidr) & addrBI,
      if (IP2.isDefined) IPv6ToBigInt(IP2.getOrElse(throw new Exception("Bad IPv6 Network Range.")))
      else addrBI | ((BigInt(1) << (128 - cidr)) - 1)
    )
  }

  // Range of the network
  lazy val range: String = s"${bigIntToIPv6(addrBIStart)}-${bigIntToIPv6(addrBIEnd)}"

  // Access operators
  lazy val networkAddress: IPv6 = bigIntToIPv6(addrBIStart)
  lazy val broadcastAddress: IPv6 = bigIntToIPv6(addrBIEnd)

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
  def contains(ip: IPAddress): Boolean = ip.addrBI >= addrBIStart && ip.addrBI <= addrBIEnd

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
  def apply(addr: IPv6): IPv6Network = IPv6Network(s"${addr.ipAddress}/128")
}
