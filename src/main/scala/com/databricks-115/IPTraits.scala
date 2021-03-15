package com.databricks115
import java.math.BigInteger
import java.net.InetAddress
import scala.math.BigInt.javaBigInteger2bigInt

trait sharedIPTraits {
    protected def longToIPv4(ip: Long): IPv4 = IPv4((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
}

trait IPv4Traits extends sharedIPTraits {
    //conversions
    protected def IPv4ToLong(ip: String): Long = {
        val fragments = ip.split('.')
        require(fragments.length == 4, "Bad IPv4")
        var ipNum = 0L
        for (i <- fragments.indices) {
            val frag2Long = fragments(i).toLong
            require(frag2Long >= 0 && frag2Long <= 255, "Bad IPv4")
            ipNum = frag2Long | ipNum << 8L
        }
        ipNum
    }
    protected def IPv4subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')
    protected def IPv4to2IPv6Octets(ip: IPv4): String = s"${(ip.addrL >> 16 & 0xFFFF).toHexString}:${(ip.addrL & 0xFFFF).toHexString}"

    //validations
    protected def IPv4Validation(ip: List[String]): Boolean = if (!ip.map(_.toInt).exists(x => x < 0 || x > 255)) true else false

}

trait IPv6Traits extends sharedIPTraits {
    //conversions
    protected def IPv6ToBigInteger(addr: String): BigInteger = {
        val i = InetAddress.getByName(addr)
        val a: Array[Byte] = i.getAddress
        new BigInteger(1, a)
    }
    protected def bigIntegerToIPv6(bi: BigInteger): IPv6 = {
        require(bi >= new BigInteger("0") && bi <= new BigInteger("340282366920938463463374607431768211455"))
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

    protected def IPv6OctetsToIPv4(octets :String): IPv4 = {
        val octet: String = octets.replace(":", "")
        longToIPv4(Integer.parseInt(octet, 16))
    }
}
