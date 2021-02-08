package com.databricks115
import org.apache.spark.sql.types.DataType

case class IPNetwork (addr: String) extends DataType {
  //to extend DataType
  override def asNullable(): DataType = return this
  override def defaultSize(): Int = return 1

  //to convert ipv4 to number
  private def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum

  //makes sure network is valid
  private def isNetwork(ip: String): Boolean = {
    //todo: cut off leading 0s or throw an error if there are leading 0s
    val IPv4 = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
    ip match {
      case IPv4(o1, o2, o3, o4, o5) =>
        !List(o1, o2, o3, o4).map(_.toInt).exists(x => x < 0 || x > 255) && (o5.toInt >=1 && o5.toInt <=32)
      case _ => false
    }
  }
  require(isNetwork(addr), "Network is invalid.")

  //parse IPv4 and subnet
  private def parseSubnet(ip: String): (String, Int) = {
    val pattern = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
    val pattern(o1, o2, o3, o4, o5) = ip
    (s"$o1.$o2.$o3.$o4", o5.toInt)
  }
  private val parsedIP: (String, Int) = parseSubnet(addr)

  //start and end of the network
  private val addrLStart: Long = 0xFFFFFFFF << (32 - parsedIP._2) & IPv4ToLong(parsedIP._1)
  private val addrLEnd: Long = addrLStart + math.pow(2, 32-parsedIP._2).toLong - 1

  def ==(that: IPNetwork): Boolean = (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)

  //checks if an IP is in the network
  def netContainsIP(ip: IPAddress): Boolean = if (ip.addrL >= addrLStart && ip.addrL <= addrLEnd) true else false

}
