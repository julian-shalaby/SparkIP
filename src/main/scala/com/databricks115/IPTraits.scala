package com.databricks115
import java.math.BigInteger

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
    protected def IPv6ToBigInteger(ip: String): BigInteger = {
        val fill = ":0:" * (8 - ip.split("::|:").count(_.nonEmpty))
        val fullArr =
            raw"((?<=\.)(\d+)|(\d+)(?=\.))".r
              .replaceAllIn(ip, _.group(1).toInt.toHexString)
              .replace("::", fill)
              .split("[:]")
              .collect { case s if s.nonEmpty => s"000$s".takeRight(4) }

        if (fullArr.length == 8) new BigInteger(fullArr.mkString, 16)
        else throw new Exception("Bad IPv6 address.")
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
