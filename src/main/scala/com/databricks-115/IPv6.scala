package com.databricks115
import org.apache.spark.sql.types.DataType
import java.math.BigInteger
import java.net.InetAddress
import scala.math.BigInt.javaBigInteger2bigInt

case class IPv6 (addr: String) extends DataType {
  //to extend DataType
  override def asNullable(): DataType = return this;
  override def defaultSize(): Int = return 1;

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

}