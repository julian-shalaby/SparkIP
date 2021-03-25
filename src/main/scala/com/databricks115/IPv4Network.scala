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
    // Address 1.1.1.1 Netmask 255.255.255.0 format
    else if (ipNetwork(0) == 'A') {
      val pattern = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".r
      val ips = pattern.findAllIn(ipNetwork)
      val addrStr = ips.next()
      val cidrString = ips.next()
      val cidrBlock = IPv4SubnetToCIDR(cidrString)
      require(isNetworkAddressInternal(cidrString, cidrBlock), "IP address must be the network address.")
      require(isNetworkAddressInternal(addrStr, cidrBlock), "Dotted decimal IP address is invalid.")
      (addrStr, cidrBlock)
    }
    // If it's an IPv6 address
    else (ipNetwork, 128)
  }

//  // Parse the network
//  private val (addr: String, cidr: Int) = {
//    //1.1.1.1/16 format
//    val NetworkCIDR: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/(\d{1,2})""".r
//    // 1.1.1.1/255.255.0.0 format
//    lazy val NetworkDottedDecimal: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
//    // If it's an IPv4 address
//    lazy val IPv4Address: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
//    // 1.1.1.1-2.2.2.2 format
//    lazy val NetworkIPRange: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\-([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
//    // Address 1.1.1.1 Netmask 255.255.255.0 format
//    lazy val NetworkVerboseDottedDecimal: Regex = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
//
//    ipNetwork match {
//      case NetworkCIDR(o1, o2, o3, o4, o5) =>
//        require(o5.toInt >= 0 && o5.toInt <= 32, "Bad IPv4 Network CIDR.")
//        val addrStr = s"$o1.$o2.$o3.$o4"
//        val cidrBlock = o5.toInt
//        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
//        (addrStr, cidrBlock)
//
//      case NetworkDottedDecimal(o1, o2, o3, o4, o5, o6, o7, o8) =>
//        val addrStr = s"$o1.$o2.$o3.$o4"
//        val cidrString = s"$o5.$o6.$o7.$o8"
//        val cidrBlock = IPv4SubnetToCIDR(s"$o5.$o6.$o7.$o8")
//        require(isNetworkAddressInternal(cidrString, cidrBlock), "Dotted decimal IP address is invalid.")
//        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
//        (addrStr, cidrBlock)
//
//      case IPv4Address(o1, o2, o3, o4) => (s"$o1.$o2.$o3.$o4", 32)
//
//      case NetworkIPRange(o1, o2, o3, o4, o5, o6, o7, o8) =>
//        IP2 = Some(s"$o5.$o6.$o7.$o8")
//        (s"$o1.$o2.$o3.$o4", -1)
//
//      case NetworkVerboseDottedDecimal(s1, o1, o2, o3, o4, s2, o5, o6, o7, o8) =>
//        val addrStr = s"$o1.$o2.$o3.$o4"
//        val cidrString = s"$o5.$o6.$o7.$o8"
//        val cidrBlock = IPv4SubnetToCIDR(s"$o5.$o6.$o7.$o8")
//        require(isNetworkAddressInternal(cidrString, cidrBlock), "Verbose dotted decimal IP address is invalid.")
//        require(isNetworkAddressInternal(addrStr, cidrBlock), "IP address must be the network address.")
//        (s"$o1.$o2.$o3.$o4", IPv4SubnetToCIDR(s"$o5.$o6.$o7.$o8"))
//
//      case _ => throw new Exception("Bad IPv4 Network Format.")
//    }
//  }

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
  def apply(addrStr: IPv4): IPv4Network = IPv4Network(s"${addrStr.ipAddress}/32")
}