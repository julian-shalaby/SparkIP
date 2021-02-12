package com.databricks115
import org.apache.spark.sql.types.DataType

//to convert ipv4 to number and vice versa
trait IPConversions {
    protected def longToIPv4(ip: Long): String = (for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString(".")
    protected def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
    protected def subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')
}

trait IPValidation {
    protected def IPv4Validation(ip: List[String]): Boolean = if (!ip.map(_.toInt).exists(x => x < 0 || x > 255)) true else false
}

/*
    Change class name to IPv4Address if IPv4 and IPv6 will be completely separate classes?
 */

trait IPAddress extends DataType with IPConversions with IPValidation{
    override def asNullable(): DataType = this
    override def defaultSize(): Int = 1

    def isIP(ip: String): Boolean    

    def mask(maskIP: String): IPAddress

    val isMulticast: Boolean
    val isPrivate: Boolean
    val isGlobal: Boolean
    val isUnspecified: Boolean
    val isLoopback: Boolean
    val isLinkLocal: Boolean
    val isReserved: Boolean
}
