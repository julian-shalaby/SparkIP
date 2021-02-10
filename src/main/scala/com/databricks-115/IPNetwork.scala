package com.databricks115
import org.apache.spark.sql.types.DataType

case class IPNetwork (addr: String) extends DataType {
  //to extend DataType
  override def asNullable(): DataType = this
  override def defaultSize(): Int = 1

  //to convert ipv4 to number
  private def longToIPv4(ip:Long): String = (for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString(".")
  private def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum

  def toBinary(n: Int): String = {
    n match {
      case 0|1 => s"$n"
      case _   => s"${toBinary(n/2)}${n%2}"
    }
  }

  //parse IPv4 and subnet
  private def parseNetwork(ip: String): (String, Int) = {
    if(ip.matches("""(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""")){
      val pattern = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
      val pattern(o1, o2, o3, o4, o5) = ip
      require(!List(o1, o2, o3, o4).map(_.toInt).exists(x => x < 0 || x > 255) && (o5.toInt >=1 && o5.toInt <=32), "Network is invalid.")
      (s"$o1.$o2.$o3.$o4", o5.toInt)
    }
    else if(ip.matches("""(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""")){
      val pattern = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
      val pattern(o1, o2, o3, o4, o5, o6, o7, o8) = ip
      require(!List(o1, o2, o3, o4, o5, o6, o7, o8).map(_.toInt).exists(x => x < 0 || x > 255), "Network is invalid.")
      val binString = s"${toBinary(o5.toInt)}${toBinary(o6.toInt)}${toBinary(o7.toInt)}${toBinary(o8.toInt)}"
      val cidr = binString.count(_ == '1')
      (s"$o1.$o2.$o3.$o4", cidr)
    }
    else ("a", 0)
  }
  private val parsedAddr: (String, Int) = parseNetwork(addr)

  //start and end of the network
  private val addrLStart: Long = 0xFFFFFFFF << (32 - parsedAddr._2) & IPv4ToLong(parsedAddr._1)
  private val addrLEnd: Long = addrLStart + math.pow(2, 32-parsedAddr._2).toLong - 1
  val range: String = s"${longToIPv4(addrLStart)}-${longToIPv4(addrLEnd)}"

  //compare networks
  def ==(that: IPNetwork): Boolean = this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd
  def !=(that: IPNetwork): Boolean = this.addrLStart != that.addrLStart || this.addrLEnd != that.addrLEnd
  def <(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd)
  }
  def >(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd)
  }
  def <=(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }
  def >=(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }

  //checks if an IP is in the network
  def netContainsIP(ip: IPAddress): Boolean = if (ip.addrL >= addrLStart && ip.addrL <= addrLEnd) true else false
}
