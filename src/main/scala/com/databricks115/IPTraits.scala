package com.databricks115
/*
    ToDo:
        1) A BigInteger to IPv6 compressed format function would be nice, but not necessary
        2) Could maybe just use BigInt and get rid of the BigInteger dependency
 */
trait IPv4Traits {
    // Validations
    // Converts an IP address into a subnet CIDR
    protected def IPv4SubnetToCIDR(subnet: String): Int = subnet.split('.').map(i => i.toInt.toBinaryString.count(_=='1')).sum
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

    protected def IPv6ToBigInt(ip: String): BigInt = {
        val expandedIPv6 = expandIPv6Internal(ip)
        BigInt(expandedIPv6.mkString, 16)
    }

    protected def bigIntToIPv6(ip: BigInt): IPv6 = IPv6((for(a<-7 to 0 by -1) yield ((ip>>(a*16))&0xffff).toString(16)).mkString(":"))

}
