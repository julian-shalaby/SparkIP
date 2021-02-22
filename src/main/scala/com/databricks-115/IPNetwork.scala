package com.databricks115

case class IPNetwork (addr: String) extends  IPConversions with IPValidation with IPRegex {

  // for if input is in range format
  private var IP2: Option[String] = None

  // parse IPv4 and subnet
  private def parseNetwork(ip: String): (String, Int) = {
    ip match {
      case IPv4Address(o1, o2, o3, o4) =>
        require(IPv4Validation(List(o1, o2, o3, o4)), "Network is invalid")
        (s"$o1.$o2.$o3.$o4", 32)

      case NetworkCIDR(o1, o2, o3, o4, o5) => 
        require(IPv4Validation(List(o1, o2, o3, o4)) && o5.toInt >= 0 && o5.toInt <= 32, "Network is invalid.")
        (s"$o1.$o2.$o3.$o4", o5.toInt)
      
      case NetworkDottedDecimal(o1, o2, o3, o4, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid.")
        (s"$o1.$o2.$o3.$o4", subnetToCidr(s"$o5.$o6.$o7.$o8"))
      
      case NetworkVerboseDottedDecimal(s1, o1, o2, o3, o4, s2, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid.")
        (s"$o1.$o2.$o3.$o4", subnetToCidr(s"$o5.$o6.$o7.$o8"))
    
      case NetworkIPRange(o1, o2, o3, o4, o5, o6, o7, o8) =>
        require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid.")
        IP2 = Some(s"$o5.$o6.$o7.$o8")
        (s"$o1.$o2.$o3.$o4", -1)
    
      case _ => throw new Exception 
    }
  }

  private val parsedAddr: (String, Int) = parseNetwork(addr)

  // start and end of the network
  private val addrLStart: Long = 0xFFFFFFFF << (32 - parsedAddr._2) & IPv4ToLong(parsedAddr._1)
  private val addrLEnd: Long = if (IP2.isDefined) IPv4ToLong(IP2.getOrElse(throw new Exception)) else addrLStart + math.pow(2, 32-parsedAddr._2).toLong - 1
  // range of the network
  val range: String = s"${longToIPv4(addrLStart)}-${longToIPv4(addrLEnd)}"

  // access operators
  def networkAddress: IPv4 = IPv4(longToIPv4(addrLStart)) 
  def broadcastAddress: IPv4 = IPv4(longToIPv4(addrLEnd))

  // compare networks
  def ==(that: IPNetwork): Boolean = this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd
  def !=(that: IPNetwork): Boolean = this.addrLStart != that.addrLStart || this.addrLEnd != that.addrLEnd
  def <(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd)
  }
  def >(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd)
  }
  def <=(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }
  def >=(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }

  // checks if an IP is in the network
  def netContainsIP(ip: IPv4): Boolean = if (ip.addrL >= addrLStart && ip.addrL <= addrLEnd) true else false
  // checks if networks overlap
  def netsIntersect(net: IPNetwork): Boolean = if (this.addrLStart <= net.addrLEnd && this.addrLEnd >= net.addrLStart) true else false
}

object IPNetwork {
  def apply(addr: IPv4) = new IPNetwork(addr.addr)
}
