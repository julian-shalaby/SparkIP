package com.databricks115
import java.math.BigInteger
import java.net.InetAddress
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6 (addr: String) extends IPAddress {
  /*
  to do:
    convert to ipv4
    variable for ipv6 mapped to ipv4?
    converts to different ipv6 formats?
    address types
    mask
    ipv6 validation checker
    converts ipv6 to a big integer
   */

  def ipToBigInteger(): BigInteger = {
      val i = InetAddress.getByName(addr)
      val a: Array[Byte] = i.getAddress
      new BigInteger(1, a)
  }
  val addrBI: BigInteger = ipToBigInteger()

  //compare operations
  def <(that: IPv6): Boolean = this.addrBI < that.addrBI
  def >(that: IPv6): Boolean = this.addrBI > that.addrBI
  def <=(that: IPv6): Boolean = this.addrBI <= that.addrBI
  def >=(that: IPv6): Boolean = this.addrBI >= that.addrBI

  //Address Types
  val isLinkLocal: Boolean = if (addrBI >= new BigInteger("338288524927261089654018896841347694592") &&
    addrBI <= new BigInteger("338620831926207318622244848606417780735")) true else false
  val isLoopback: Boolean = if (addrBI == new BigInteger("1")) true else false
  val isMulticast: Boolean = if (addrBI >= new BigInteger("338953138925153547590470800371487866880") &&
    addrBI <= new BigInteger("340282366920938463463374607431768211455")) true else false
  val isUnspecified: Boolean = if (addrBI == new BigInteger("0")) true else false
  val isUniqueLocal: Boolean = if (addrBI >= new BigInteger("334965454937798799971759379190646833152") &&
    addrBI <= new BigInteger("337623910929368631717566993311207522303")) true else false
  val isReserved: Boolean = if (
    isUnspecified ||
      isLoopback ||
      (addrBI >= new BigInteger("281470681743360") && addrBI <= new BigInteger("281474976710655")) ||
      (addrBI >= new BigInteger("18446462598732840960") && addrBI <= new BigInteger("18446462603027808255")) ||
      (addrBI >= new BigInteger("524413980667603649783483181312245760") && addrBI <= new BigInteger("524413980667603649783483185607213055")) ||
      (addrBI >= new BigInteger("1329227995784915872903807060280344576") && addrBI <= new BigInteger("1329227995784915891350551133989896191")) ||
      (addrBI >= new BigInteger("42540488161975842760550356425300246528") && addrBI <= new BigInteger("42540488241204005274814694018844196863")) ||
      (addrBI >= new BigInteger("42540490697277043217009159418706657280") && addrBI <= new BigInteger("42540491964927643445238560915409862655")) ||
      (addrBI >= new BigInteger("42540766411282592856903984951653826560") && addrBI <= new BigInteger("42540766490510755371168322545197776895")) ||
      (addrBI >= new BigInteger("42545680458834377588178886921629466624") && addrBI <= new BigInteger("42550872755692912415807417417958686719")) ||
      isUniqueLocal ||
      isLinkLocal ||
      isMulticast
  ) true else false

  /*
    Stub Functions
    Impl Later
   */
  def isIP(ip: String): Boolean = false




  def mask(maskIP: String): com.databricks115.IPAddress = IPv6("0")
  def toNetwork: IPNetwork = IPNetwork("0.0.0.0")

  override val isPrivate: Boolean = false
  override val isGlobal: Boolean = false
}