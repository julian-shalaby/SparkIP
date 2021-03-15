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
}

trait IPv6Traits {
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
}
