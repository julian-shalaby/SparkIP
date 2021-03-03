package com.databricks115
import org.apache.spark.sql.types.DataType
import scala.util.matching.Regex

//to convert ipv4 to number and vice versa
trait IPConversions {
    protected def longToIPv4(ip: Long): String = (for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString(".")
    protected def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
    protected def IPv4subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')
    //test this
    protected def IPv6subnetToCidr(subnet: String): Int = 128-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*16}.sum.toBinaryString.count(_ =='0')
}

trait IPValidation {
    protected def IPv4Validation(ip: List[String]): Boolean = if (!ip.map(_.toInt).exists(x => x < 0 || x > 255)) true else false
}

trait IPRegex {
    protected val IPv4Address: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    protected val IPv6Address: Regex = """([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])""".r

    //1.1.1.1/16 format
    protected val NetworkCIDR: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d)""".r
    protected val IPv6NetworkCIDR: Regex = """([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3}):([a-f0-9]|[a-f1-9][a-f0-9]{1,3})\/([0-9]|[1-9]{1,2})""".r

    //1.1.1.1/255.255.0.0 format
    protected val NetworkDottedDecimal: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    
    //Address 1.1.1.1 Netmask 255.255.255.0 format
    protected val NetworkVerboseDottedDecimal: Regex = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
    
    //1.1.1.1-2.2.2.2 format
    protected val NetworkIPRange: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\-([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
}

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
