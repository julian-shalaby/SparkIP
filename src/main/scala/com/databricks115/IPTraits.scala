package com.databricks115
import java.math.BigInteger
/*
    ToDo:
        1) A BigInteger to IPv6 compressed format function would be nice, but not necessary
        2) Could maybe just use BigInt and get rid of the BigInteger dependency
 */
trait IPv4Traits {
    // Validations
    // Converts an IP address into a subnet CIDR
    protected def IPv4SubnetToCIDR(subnet: String): Int = 32 - subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
      map { case (value, index) => value << index * 8 }.sum.toBinaryString.count(_ == '0')
    // Checks whether a IP address is the network address of this network
    protected def isNetworkAddressInternal(addrStr: String, cidrBlock: Int): Boolean = {
        val ip = IPv4(addrStr)
        val netAddr = ip.mask(cidrBlock)
        ip == netAddr
    }

    // Conversions
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

    // Conversions
    protected def expandIPv6Internal(ip: String): Array[String] = {
        val fill = ":0:" * (8 - ip.split("::|:").count(_.nonEmpty))
        val expandedIPv6 =
            raw"((?<=\.)(\d+)|(\d+)(?=\.))".r
              .replaceAllIn(ip, _.group(1).toInt.toHexString)
              .replace("::", fill)
              .split("[:]")
              .collect { case s if s.nonEmpty =>
                  require(s.matches("[0-9a-f]{1,4}"), "Bad IPv6 address.")
                  s"000$s".takeRight(4)
              }
        require(expandedIPv6.length == 8, "Bad IPv6 address.")
        expandedIPv6
    }
    protected def expandIPv6(ip: String): String = {
        val expandedIPv6 = expandIPv6Internal(ip)
        s"${expandedIPv6(0)}:${expandedIPv6(1)}:${expandedIPv6(2)}:${expandedIPv6(3)}:${expandedIPv6(4)}:${expandedIPv6(5)}:${expandedIPv6(6)}:${expandedIPv6(7)}"
    }

    protected def IPv6ToBigInteger(ip: String): BigInteger = {
        val expandedIPv6 = expandIPv6Internal(ip)
        new BigInteger(expandedIPv6.mkString, 16)
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
}
