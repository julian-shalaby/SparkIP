package com.databricks115

case class IPv6 (ipAddress: String) extends Ordered[IPv6] with IPv6Traits with IPv4Traits {
  // IPv6 as a number
  val addrBI: BigInt = IPv6ToBigInt(ipAddress)

  // Compare operations
  override def <(that: IPv6): Boolean = this.addrBI < that.addrBI
  override def >(that: IPv6): Boolean = this.addrBI > that.addrBI
  override def <=(that: IPv6): Boolean = this.addrBI <= that.addrBI
  override def >=(that: IPv6): Boolean = this.addrBI >= that.addrBI
  def ==(that: IPv6): Boolean = this.addrBI == that.addrBI
  def !=(that: IPv6): Boolean = this.addrBI != that.addrBI
  def compare(that: IPv6): Int = {
    if (this.addrBI == that.addrBI) 0
    else if (this.addrBI < that.addrBI) -1
    else 1
  }
  
  // Address Types
  lazy val isLinkLocal: Boolean = addrBI >= BigInt("338288524927261089654018896841347694592") &&
    addrBI <= BigInt("338620831926207318622244848606417780735")
  lazy val isLoopback: Boolean = addrBI == 1
  lazy val isMulticast: Boolean = addrBI >= BigInt("338953138925153547590470800371487866880") &&
    addrBI <= BigInt("340282366920938463463374607431768211455")
  lazy val isUnspecified: Boolean = addrBI == 0
  lazy val isUniqueLocal: Boolean = addrBI >= BigInt("334965454937798799971759379190646833152") &&
    addrBI <= BigInt("337623910929368631717566993311207522303")
  lazy val isIPv4Mapped: Boolean = addrBI >= 281470681743360L &&
    addrBI <= 281474976710655L
  lazy val isIPv4Translated: Boolean = addrBI >= BigInt("18446462598732840960") &&
    addrBI <= BigInt("18446462603027808255")
  lazy val isIPv4IPv6Translated: Boolean = addrBI >= BigInt("524413980667603649783483181312245760") &&
    addrBI <= BigInt("524413980667603649783483185607213055")
  lazy val isTeredo: Boolean = addrBI >= BigInt("42540488161975842760550356425300246528") &&
    addrBI <= BigInt("42540488241204005274814694018844196863")
  lazy val is6to4: Boolean = addrBI >= BigInt("42545680458834377588178886921629466624") &&
    addrBI <= BigInt("42550872755692912415807417417958686719")
  lazy val isReserved: Boolean = isUnspecified || isLoopback || isIPv4Mapped || isIPv4Translated || isIPv4IPv6Translated ||
    (addrBI >= BigInt("1329227995784915872903807060280344576") && addrBI <= BigInt("1329227995784915891350551133989896191")) ||
    isTeredo ||
    (addrBI >= BigInt("42540490697277043217009159418706657280") && addrBI <= BigInt("42540491964927643445238560915409862655")) ||
    (addrBI >= BigInt("42540766411282592856903984951653826560") && addrBI <= BigInt("42540766490510755371168322545197776895")) ||
    is6to4 || isUniqueLocal || isLinkLocal || isMulticast

  // Return network address of IP address
  def mask(maskIP: Int): IPv6 = {
    require(maskIP >= 0 && maskIP <= 128, "Can only mask 0-128.")
    bigIntToIPv6(BigInt("340282366920938463463374607431768211455") << (128 - maskIP) & addrBI)
  }

  // Interface with ipv4
  private def IPv6OctetsToIPv4(octets :String): IPv4 = {
    val octet: String = octets.filter(_!=':')
    longToIPv4(Integer.parseInt(octet, 16))
  }

  def sixToFour: IPv4 = {
    require(is6to4, "Not a 6to4 address.")
    val octet1 = ipAddress.split(':')(1)
    val octet2 = ipAddress.split(':')(2)
    IPv6OctetsToIPv4(s"$octet1:$octet2")
  }

  def IPv4Mapped: IPv4 = {
    require(isIPv4Mapped, "Not a IPv4 mapped address.")
    val expandedIPv6 = expandIPv6Internal(ipAddress)
    val octet1 = expandedIPv6(6)
    val octet2 = expandedIPv6(7)
    IPv6OctetsToIPv4(s"$octet1:$octet2")
  }

  def teredoServer: IPv4 = {
    require(isTeredo, "Not a teredo address.")
    val expandedIPv6 = expandIPv6Internal(ipAddress)
    val octet1 = expandedIPv6(2)
    val octet2 = expandedIPv6(3)
    IPv6OctetsToIPv4(s"$octet1:$octet2")
  }
  def teredoClient: IPv4 = {
    require(isTeredo, "Not a teredo address.")
    val expandedIPv6 = expandIPv6Internal(ipAddress)
    val octet1 = expandedIPv6(6)
    val octet2 = expandedIPv6(7)
    val toV4 = IPv6OctetsToIPv4(s"$octet1:$octet2")
    longToIPv4(4294967295L ^ BigInt(s"${IPv4ToLong(toV4.ipAddress)}").toLong)
  }

}