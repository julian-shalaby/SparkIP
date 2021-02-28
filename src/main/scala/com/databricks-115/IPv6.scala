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


  /*
    Stub Functions
    Impl Later
   */
  val isGlobal: Boolean = false
  def isIP(ip: String): Boolean = false
  val isLinkLocal: Boolean = false
  val isLoopback: Boolean = false
  val isMulticast: Boolean = false
  val isPrivate: Boolean = false
  val isReserved: Boolean = false
  val isUnspecified: Boolean = false
  def mask(maskIP: String): com.databricks115.IPAddress = IPv6("0")
  def toNetwork: IPNetwork = IPNetwork("0.0.0.0")
}