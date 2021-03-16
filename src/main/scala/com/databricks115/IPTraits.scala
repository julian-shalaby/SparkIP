package com.databricks115
import java.math.BigInteger
import java.net.InetAddress
//import scala.math.BigInt.javaBigInteger2bigInt

trait IPv4Traits {
    //conversions
    protected def longToIPv4(ip: Long): IPv4 = IPv4((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
    protected def IPv4ToLong(ip: String): Long = {
        val fragments = ip.split('.')
        require(fragments.length == 4, "Bad IPv4 address.")
        var ipNum = 0L
        for (i <- fragments.indices) {
            val frag2Long = fragments(i).toLong
            require(!(fragments(i).length > 1 && frag2Long == 0), "Bad IPv4 address.")
            require(frag2Long >= 0 && frag2Long <= 255, "Bad IPv4 address.")
            ipNum = frag2Long | ipNum << 8L
        }
        ipNum
    }
}

trait IPv6Traits {
    //conversions
    //gives wrong output sometimes for some reason
     def IPv6ToBigInteger(ip: String): BigInteger = new BigInteger(1, InetAddress.getByName(ip).getAddress)


    /*
        New iptobigint. have parsing problem
     */

//    protected def IPv6ToBigInteger(ip: String): BigInteger = {
//        val fragments = ip.split(":|\\.|::").filter(_.nonEmpty)
//        require(fragments.length <= 8, "Bad IPv6 address.")
//        var ipNum = new BigInteger("0")
//        for (i <-fragments.indices) {
//            val frag2Long = new BigInteger(s"${fragments(i)}", 16)
//            ipNum = frag2Long.or(ipNum).shiftLeft(16)
//        }
//        ipNum
//    }

     def bigIntegerToIPv6(bi: BigInteger): IPv6 = {
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
