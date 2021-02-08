package com.databricks115
import org.apache.spark.sql.types.DataType

case class IPNetwork (addr: String) extends DataType {
  //to extend DataType
  override def asNullable(): DataType = return this
  override def defaultSize(): Int = return 1

  //to convert ipv4 to number and vice versa
  private def longToIPv4(ip:Long) = (for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString(".")
  private def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum

  //Return network address of IP address
  //error prone and ugly
  def mask(ip: String, maskIP: Int): String = {
    val mask = (0xFFFFFFFF << (32 - maskIP.toString.toInt)) & 0xFFFFFFFF
    val mask2 = s"${mask >> 24 & 0xFF}.${(mask >> 16) & 0xFF}.${(mask >> 8) & 0xFF}.${mask & 0xFF}"
    longToIPv4(IPv4ToLong(mask2) & IPv4ToLong(ip))
  }

  //take in 1.1.1.1/16 form and find its network address
  //also error prone and ugly
  def networkAddress(ip: String): String = {
    val pattern = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
    val pattern(o1, o2, o3, o4, o5) = ip
    val temp = s"${o1}.${o2}.${o3}.${o4}"
    mask(temp, o5.toInt)
  }
  val addrLStart: Long = IPv4ToLong(networkAddress(addr))
  //val addrLEnd: Long = addrLStart + math.pow(2, prefix).toLong

}
