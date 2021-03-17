package com.databricks115
import java.math.BigInteger

/*
    ToDo:
        1) Make a function that formats IPv6 addresses into full address format (2001:: to 2001:0:0:0:0:0:0:0)
        2) Make a function that formats IPv6 addresses into shortened address format (2001:0:0:0:0:0:0:0 to 2001::)
       (1&2)-> Will be used in IPv6ToBigInteger, BigIntegerToIPv6, sixToFour, IPv4Mapped, teredoServer, and teredoClient
            -> Must handle ::ffff, ffff::, and ffff::ffff formats. Must also check that there are:
                <= 8 octets, each letter is lowercase, each letter is <= f, and each octet is <= 4 characters
                   make sure :: is a valid input too
       3) Maybe make a case object allowing users to JUST access some of these functions if they want? (IP.IPv4ToLong("192.0.0.0"))
       4) Could potentially (but probably not) redo longToIPv4 and bigIntegerToIPv6 to make them more efficient
 */

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
    /*
    //might be better but needs parsing

    protected def IPv6ToBigInteger(ip: String): BigInteger = {
        val fragments = ip.split(":|\\.|::").filter(_.nonEmpty)
        require(fragments.length <= 8, "Bad IPv6 address.")
        var ipNum = new BigInteger("0")
        for (i <-fragments.indices) {
            val frag2Long = new BigInteger(s"${fragments(i)}", 16)
            ipNum = frag2Long.or(ipNum).shiftLeft(16)
        }
        ipNum
    }
     */
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
