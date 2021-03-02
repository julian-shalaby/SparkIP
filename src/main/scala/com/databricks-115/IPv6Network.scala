package com.databricks115
import org.apache.spark.sql.types.DataType
import java.math.BigInteger
import java.net.InetAddress
import scala.math.BigInt.javaBigInteger2bigInt

/*
ToDo:
  parseNetwork:
    Like ipv4 networks. Need to read input in the forms:
      1) (ipv6)/(cidr) //ipv6 cidr goes to 128
      2) (ipv6)/(ipv6 network address)
      3) (ipv6)/(ipv6) //range format
    then parse into string/int

 */

case class IPv6Network (addr: String) extends DataType with IPConversions with IPValidation with IPRegex {
  // to extend DataType
  override def asNullable(): DataType = this
  override def defaultSize(): Int = 1

//  private def parseNetwork(ip: String): (String, Int) = ip match {
//    case NetworkCIDR(o1, o2, o3, o4, o5, o6, o7, o8, o9) =>
//      val addrStr: String = s"$o1:$o2:$o3:$o4:$o5:$o6:$o7:$o8"
//      val cidrBlock: Int = o9.toInt
//      (addrStr, cidrBlock)
//
//    case _ => throw new Exception
//  }

  private val parsedAddr: (String, Int) = (addr, 32)

  def IPv6ToBigInteger(addr: String): BigInteger = {
    val i = InetAddress.getByName(addr)
    val a: Array[Byte] = i.getAddress
    new BigInteger(1, a)
  }
  def bigIntegerToIPv6(ipv6Num : BigInteger) : String = {
    val ipv6Str = InetAddress.getByAddress(ipv6Num.toByteArray).toString
    ipv6Str.replaceFirst("/", "")
  }

  val addrBIStart: BigInteger = new BigInteger("340282366920938463463374607431768211455")
    .shiftLeft(new BigInteger("128").subtract(new BigInteger(s"${parsedAddr._2}")).toInt)
    .and(IPv6ToBigInteger(parsedAddr._1))
  val addrBIEnd: BigInteger = addrBIStart
    .add(new BigInteger("2").pow(128-parsedAddr._2))
    .subtract(new BigInteger("1"))

  // range of the network
  val range: String = s"${bigIntegerToIPv6(addrBIStart)}-${bigIntegerToIPv6(addrBIEnd)}"

  // access operators
  lazy val networkAddress: IPv6 = IPv6(bigIntegerToIPv6(addrBIStart))
  lazy val broadcastAddress: IPv6 = IPv6(bigIntegerToIPv6(addrBIEnd))


  // compare networks
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

  // checks if an IP is in the network
  def netContainsIP(ip: IPv6): Boolean = if (ip.addrBI >= addrBIStart && ip.addrBI <= addrBIEnd) true else false

  // checks if networks overlap
  def netsIntersect(net: IPv6Network): Boolean = if (this.addrBIStart <= net.addrBIEnd && this.addrBIEnd >= net.addrBIStart) true else false

  // checks whether a ip address is the network address of this network
  def isNetworkAddress(addr: String): Boolean = isNetworkAddressInternal(addr, parsedAddr._2)
  private def isNetworkAddressInternal(addrStr: String, cidrBlock: Int) = {
    val ip: IPv6 = IPv6(addrStr)
    val netAddr: IPv6 = ip.mask(cidrBlock)
    ip == netAddr
  }

}

object IPv6Network {
  def apply(addr: IPv6) = new IPv6Network(addr.addr)
}
