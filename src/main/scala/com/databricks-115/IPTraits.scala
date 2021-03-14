package com.databricks115
import org.apache.spark.sql.types.DataType
import java.math.BigInteger
import java.net.InetAddress
import scala.util.matching.Regex

trait IPv4Traits {
    //conversions
    protected def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
    protected def IPv4subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')

    //validations
    protected def IPv4Validation(ip: List[String]): Boolean = if (!ip.map(_.toInt).exists(x => x < 0 || x > 255)) true else false
    //makes sure IPv4 is valid
    def isIP(ip: String): Boolean = {
        ip match {
            case IPv4Address(o1, o2, o3, o4) => IPv4Validation(List(o1, o2, o3, o4))
            case _ => false
        }
    }

    //regex
    val IPv4Address: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    //1.1.1.1/16 format
    protected val NetworkCIDR: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,2})""".r
//    //1.1.1.1/255.255.0.0 format
//    protected val NetworkDottedDecimal: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\/(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
//    //Address 1.1.1.1 Netmask 255.255.255.0 format
//    protected val NetworkVerboseDottedDecimal: Regex = """(^Address )(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})( Netmask )(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
//    //1.1.1.1-2.2.2.2 format
//    protected val NetworkIPRange: Regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\-(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r

}

trait IPv6Traits {
    //conversions
    protected def IPv6ToBigInteger(addr: String): BigInteger = {
        val i = InetAddress.getByName(addr)
        val a: Array[Byte] = i.getAddress
        new BigInteger(1, a)
    }
    protected def bigIntegerToIPv6(bi: BigInteger): IPv6 = {
        IPv6(String.format("%s:%s:%s:%s:%s:%s:%s:%s",
            Integer.toHexString(bi.shiftRight(112).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(96).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(80).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(64).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(48).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(32).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.shiftRight(16).and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String,
            Integer.toHexString(bi.and(BigInteger.valueOf(0xFFFF)).intValue): java.lang.String))
    }

    //regex
    protected val IPv6Address: Regex = """([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])""".r
    protected val IPv6NetworkCIDR: Regex = """(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))\/(\d{1,3})""".r
    protected val IPv6NetworkRange: Regex = """(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))\-(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))""".r

}

trait sharedIPTraits extends DataType {
    override def asNullable(): DataType = this
    override def defaultSize(): Int = 1
    protected def longToIPv4(ip: Long): IPv4 = IPv4((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
    protected def IPv4to2IPv6Octets(ip: IPv4): String = s"${(ip.addrL >> 16 & 0xFFFF).toHexString}:${(ip.addrL & 0xFFFF).toHexString}"
    protected def IPv6OctetsToIPv4(octets :String): IPv4 = {
        val octet: String = octets.replace(":", "")
        longToIPv4(Integer.parseInt(octet, 16))
    }
}
