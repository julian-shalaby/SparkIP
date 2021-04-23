package com.databricks115

/*
  ToDo:
    1) use Either[Long, BigInt] to store addresses based on number size
    2) Add interface functions
 */

case class IPAddress(ipAddress: String) extends IPTraits with Ordered[IPAddress] {
  // IP as a number
  val (addrBI: BigInt, isV4: Boolean, isV6: Boolean) = IPToBigInt(ipAddress)

  // Compare operations
  override def <(that: IPAddress): Boolean = this.addrBI < that.addrBI
  override def >(that: IPAddress): Boolean = this.addrBI > that.addrBI
  override def <=(that: IPAddress): Boolean = this.addrBI <= that.addrBI
  override def >=(that: IPAddress): Boolean = this.addrBI >= that.addrBI
  def ==(that: IPAddress): Boolean = this.addrBI == that.addrBI
  def !=(that: IPAddress): Boolean = this.addrBI != that.addrBI
  def compare(that: IPAddress): Int = {
    if (this.addrBI == that.addrBI) 0
    else if (this.addrBI < that.addrBI) -1
    else 1
  }

  // Address types
  lazy val isPrivate: Boolean = isV4 && (addrBI >= 167772160L && addrBI <= 184549375L) ||
    (addrBI >= 2886729728L && addrBI <= 2887778303L) ||
    (addrBI >= 3232235520L && addrBI <= 3232301055L)
  lazy val isGlobal: Boolean = isV4 && !isPrivate
  lazy val isLinkLocal: Boolean = {
    if (isV6 &&
      addrBI >= BigInt("338288524927261089654018896841347694592") &&
        addrBI <= BigInt("338620831926207318622244848606417780735")) true
    else if (isV4 && addrBI >= 2851995648L && addrBI <= 2852061183L) true
    else false
  }
  lazy val isLoopback: Boolean = {
    if (isV6 && addrBI == 1) true
    else if (isV4 && addrBI >= 2130706432L && addrBI <= 2147483647L) true
    else false
  }
  lazy val isMulticast: Boolean = {
    if (isV6 &&
    addrBI >= BigInt("338953138925153547590470800371487866880") &&
      addrBI <= BigInt("340282366920938463463374607431768211455")) true
    else if (isV4 && addrBI >= 3758096384L && addrBI <= 4026531839L) true
    else false
  }
  lazy val isUnspecified: Boolean = addrBI == 0
  lazy val isUniqueLocal: Boolean =  isV6 && addrBI >= BigInt("334965454937798799971759379190646833152") &&
    addrBI <= BigInt("337623910929368631717566993311207522303")
  lazy val isIPv4Mapped: Boolean = isV6 && addrBI >= 281470681743360L &&
    addrBI <= 281474976710655L
  lazy val isIPv4Translated: Boolean = isV6 && addrBI >= BigInt("18446462598732840960") &&
    addrBI <= BigInt("18446462603027808255")
  lazy val isIPv4IPv6Translated: Boolean = isV6 && addrBI >= BigInt("524413980667603649783483181312245760") &&
    addrBI <= BigInt("524413980667603649783483185607213055")
  lazy val isTeredo: Boolean = isV6 && addrBI >= BigInt("42540488161975842760550356425300246528") &&
    addrBI <= BigInt("42540488241204005274814694018844196863")
  lazy val is6to4: Boolean = isV6 && addrBI >= BigInt("42545680458834377588178886921629466624") &&
    addrBI <= BigInt("42550872755692912415807417417958686719")
//  lazy val isReserved: Boolean = isUnspecified || isLoopback || isIPv4Mapped || isIPv4Translated || isIPv4IPv6Translated ||
//    (addrBI >= BigInt("1329227995784915872903807060280344576") && addrBI <= BigInt("1329227995784915891350551133989896191")) ||
//    isTeredo ||
//    (addrBI >= BigInt("42540490697277043217009159418706657280") && addrBI <= BigInt("42540491964927643445238560915409862655")) ||
//    (addrBI >= BigInt("42540766411282592856903984951653826560") && addrBI <= BigInt("42540766490510755371168322545197776895")) ||
//    is6to4 || isUniqueLocal || isLinkLocal || isMulticast

}