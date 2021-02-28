package com.databricks115
import org.apache.spark.sql.types.DataType
import java.math.BigInteger

/*
ToDo:
  parseNetwork:
    Like ipv4 networks. Need to read input in the forms:
      1) (ipv6)/(cidr) //ipv6 cidr goes to 128
      2) (ipv6)/(ipv6 network address)
      3) (ipv6)/(ipv6) //range format
    then parse into string/int

  addrBIStart/addrBIEnd:
    Get the network and broadcast addresses

  compare networks:
    operators to compare networks (worry about == most. rest might be unnecessary)

  netContainsIP:
    Check if an IP is in this network

  netsIntersect:
    Check if this network intersects with another

 */

case class IPv6Network (addr: String) extends DataType with IPConversions with IPValidation with IPRegex {
  // to extend DataType
  override def asNullable(): DataType = this
  override def defaultSize(): Int = 1

  // parse IPv4 and subnet
  private def parseNetwork(ip: String): (String, Int) = ("",0)

  private val parsedAddr: (String, Int) = parseNetwork(addr)

  // start and end of the network
  private val addrBIStart: BigInteger = ???
  private val addrBIEnd: BigInteger = ???

  // compare networks
  def ==(that: IPv6Network): Boolean = ???
  def !=(that: IPv6Network): Boolean = ???
  def <(that: IPv6Network): Boolean = ???
  def >(that: IPv6Network): Boolean = ???
  def <=(that: IPv6Network): Boolean = ???
  def >=(that: IPv6Network): Boolean = ???

  // checks if an IP is in the network
  def netContainsIP(ip: IPv6): Boolean = ???

  // checks if networks overlap
  def netsIntersect(net: IPv6Network): Boolean = ???

}

object IPv6Network {
  def apply(addr: IPv6) = new IPv6Network(addr.addr)
}
