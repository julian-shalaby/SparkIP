package com.databricks115
import java.math.BigInteger
import java.net.InetAddress
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6 (addr: String) extends IPAddress with Ordered[IPv6] with IPRegex {
  /*
  ToDo:
    6to4:
      Function that takes an IPv6 address and converts it to IPv4
      is6to4 has to be true

    IPv4Mapped:
      Function that takes an IPv6 address and converts it to IPv4
      isIPv4Mapped has to be true
      Might actually need to be in IPv4 and convert to IPv6. read into first

    //Maybe make functions similar to 6to4 for IPv4/IPv6 translation and IPv4 translated addresses

   */

  def IPv6ToBigInteger(addr: String): BigInteger = {
    val i = InetAddress.getByName(addr)
    val a: Array[Byte] = i.getAddress
    new BigInteger(1, a)
  }
  def bigIntegerToIPv6(ipv6Num : BigInteger) : String = {
    val ipv6Str = InetAddress.getByAddress(ipv6Num.toByteArray).toString
    ipv6Str.replaceFirst("/", "")
  }
  val addrBI: BigInteger = IPv6ToBigInteger(addr)

  //compare operations
  override def <(that: IPv6): Boolean = this.addrBI < that.addrBI
  override def >(that: IPv6): Boolean = this.addrBI > that.addrBI
  override def <=(that: IPv6): Boolean = this.addrBI <= that.addrBI
  override def >=(that: IPv6): Boolean = this.addrBI >= that.addrBI

  //Address Types
  val isLinkLocal: Boolean = if (addrBI >= new BigInteger("338288524927261089654018896841347694592") &&
    addrBI <= new BigInteger("338620831926207318622244848606417780735")) true else false
  val isLoopback: Boolean = if (addrBI == new BigInteger("1")) true else false
  val isMulticast: Boolean = if (addrBI >= new BigInteger("338953138925153547590470800371487866880") &&
    addrBI <= new BigInteger("340282366920938463463374607431768211455")) true else false
  val isUnspecified: Boolean = if (addrBI == new BigInteger("0")) true else false
  val isUniqueLocal: Boolean = if (addrBI >= new BigInteger("334965454937798799971759379190646833152") &&
    addrBI <= new BigInteger("337623910929368631717566993311207522303")) true else false
  val isIPv4Mapped: Boolean = if (addrBI >= new BigInteger("281470681743360") &&
    addrBI <= new BigInteger("281474976710655")) true else false
  val isIPv4Translated: Boolean = if (addrBI >= new BigInteger("18446462598732840960") &&
    addrBI <= new BigInteger("18446462603027808255")) true else false
  val isIPv4IPv6Translated: Boolean = if (addrBI >= new BigInteger("524413980667603649783483181312245760") &&
    addrBI <= new BigInteger("524413980667603649783483185607213055")) true else false
  val isTeredo: Boolean = if (addrBI >= new BigInteger("42540488161975842760550356425300246528") &&
    addrBI <= new BigInteger("42540488241204005274814694018844196863")) true else false
  val is6to4: Boolean = if (addrBI >= new BigInteger("42545680458834377588178886921629466624") &&
    addrBI <= new BigInteger("42550872755692912415807417417958686719")) true else false
  val isReserved: Boolean = if (
    isUnspecified ||
      isLoopback ||
      isIPv4Mapped ||
      isIPv4Translated ||
      isIPv4IPv6Translated ||
      (addrBI >= new BigInteger("1329227995784915872903807060280344576") && addrBI <= new BigInteger("1329227995784915891350551133989896191")) ||
      isTeredo ||
      (addrBI >= new BigInteger("42540490697277043217009159418706657280") && addrBI <= new BigInteger("42540491964927643445238560915409862655")) ||
      (addrBI >= new BigInteger("42540766411282592856903984951653826560") && addrBI <= new BigInteger("42540766490510755371168322545197776895")) ||
      is6to4 ||
      isUniqueLocal ||
      isLinkLocal ||
      isMulticast
  ) true else false

  //Mask
  def mask(maskIP: Int): IPv6 = {
    require(maskIP >= 0 && maskIP <= 128, "Can only mask 0-128.")
    IPv6(bigIntegerToIPv6(
      new BigInteger("340282366920938463463374607431768211455")
        .shiftLeft(new BigInteger("128").subtract(new BigInteger(s"$maskIP")).toInt)
        .and(addrBI)
    ))
  }
  def mask(maskIP: String): IPv6 = {
    IPv6(bigIntegerToIPv6(IPv6ToBigInteger(maskIP).and(addrBI)))
  }

  def toNetwork: IPv6Network = IPv6Network(addr)

  //6to4

  /*
    Stub Functions
    Impl Later
   */

  override val isPrivate: Boolean = false
  override val isGlobal: Boolean = false

  override def compare(that: IPv6): Int = (this.addrBI - that.addrBI).toInt

  override def isIP(ip: String): Boolean = ???
}

/*
IPv4-mapped addresses:
  ::ffff:0.0.0.0 to ::ffff:255.255.255.255
  281470681743360 to 281474976710655

IPv4 translated addresses:
  ::ffff:0:0.0.0.0 to ::ffff:0:255.255.255.255
  18446462598732840960 to 18446462603027808255

IPv4/IPv6 translation:
  64:ff9b::0.0.0.0 to 64:ff9b::255.255.255.255
  524413980667603649783483181312245760 to 524413980667603649783483185607213055

Teredo:
  2001:: to 2001::ffff:ffff:ffff:ffff:ffff:ffff
  42540488161975842760550356425300246528 to 42540488241204005274814694018844196863

6to4:
  2002:: to 2002:ffff:ffff:ffff:ffff:ffff:ffff:ffff
  42545680458834377588178886921629466624 to 42550872755692912415807417417958686719

 */