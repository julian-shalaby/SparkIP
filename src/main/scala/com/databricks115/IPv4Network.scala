package com.databricks115

case class IPv4Network(ipNetwork: String) extends IPv4Traits {
  // If input is in range format
  private var IP2: Option[String] = None

  // Parse the network
  private val (addr: String, cidr: Int) = {
    // 192.0.0.0/16 format
    val cidrSplit = ipNetwork.split('/')
    // 192.0.0.0-255.0.0.0 format
    lazy val rangeSplit = ipNetwork.split('-')

    if (cidrSplit.length == 2) {
      // if the length of the subnet is <= 2, it has to be /16 format
      if (cidrSplit(1).length <= 2) {
        val cidrBlock = cidrSplit(1).toInt
        require(isNetworkAddressInternal(cidrSplit(0), cidrBlock), "IP address must be the network address.")
        require(cidrBlock >= 0 && cidrBlock <= 32, "Bad IPv6 Network CIDR.")
        (cidrSplit(0), cidrBlock)
      }
      // if the length of the subnet is > 2, it has to be /255.0.0.0 format
      else {
        val cidrBlock = IPv4SubnetToCIDR(cidrSplit(1))
        require(isNetworkAddressInternal(cidrSplit(1), cidrBlock), "IP address must be the network address.")
        require(isNetworkAddressInternal(cidrSplit(0), cidrBlock), "Dotted decimal IP address is invalid.")
        (cidrSplit(0), cidrBlock)
      }
    }
    else if (rangeSplit.length == 2) {
      IP2 = Some(rangeSplit(1))
      (rangeSplit(0), -1)
    }
    // If it's an IPv6 address
    else (ipNetwork, 128)
  }

  // Start and end of the network
  val (addrLStart: Long, addrLEnd: Long) = {
    val addrL = IPv4ToLong(addr)
    (if (IP2.isDefined) addrL else 0xFFFFFFFF << (32 - cidr) & addrL,
      if (IP2.isDefined) IPv4ToLong(IP2.getOrElse(throw new Exception("Bad IPv4 Network Range."))) else addrL | ((1 << (32 - cidr)) - 1))
  }

  // Range of the network
  lazy val range: String = s"${longToIPv4(addrLStart)}-${longToIPv4(addrLEnd)}"

  // Access operators
  lazy val networkAddress: IPv4 = longToIPv4(addrLStart)
  lazy val broadcastAddress: IPv4 = longToIPv4(addrLEnd)

  // Compare networks
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

  // Checks if an IP is in the network
  def contains(ip: IPv4): Boolean = ip.addrL >= addrLStart && ip.addrL <= addrLEnd

  // Checks if networks overlap
  def netsIntersect(net: IPv4Network): Boolean = this.addrLStart <= net.addrLEnd && this.addrLEnd >= net.addrLStart
}

object IPv4Network {
  def apply(ipv4: IPv4): IPv4Network = IPv4Network(s"${ipv4.ipAddress}/32")
}