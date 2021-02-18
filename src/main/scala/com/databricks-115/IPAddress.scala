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

trait IPRegex {
    protected val IPv4Address = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    
    //1.1.1.1/16 format
    protected val NetworkCIDR = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d)""".r
    
    //1.1.1.1/255.255.0.0 format
    protected val NetworkDottedDecimal = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    
    //Address 1.1.1.1 Netmask 255.255.255.0 format
    protected val NetworkVerboseDottedDecimal = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    
    //1.1.1.1-2.2.2.2 format
    protected val NetworkIPRange = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\-([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
}

trait IPAddress extends DataType with IPConversions with IPValidation{
    override def asNullable(): DataType = this
    override def defaultSize(): Int = 1

    def isIP(ip: String): Boolean    
    def mask(maskIP: String): IPAddress
    def toNetwork: IPNetwork

    val isMulticast: Boolean
    val isPrivate: Boolean
    val isGlobal: Boolean
    val isUnspecified: Boolean
    val isLoopback: Boolean
    val isLinkLocal: Boolean
    val isReserved: Boolean
}
