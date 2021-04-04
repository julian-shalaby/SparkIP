package com.databricks115

trait IPv4Traits {
    protected def IPv4SubnetToCIDR(subnet: String): Int = subnet.split('.').map(i => i.toInt.toBinaryString.count(_=='1')).sum
    protected def isNetworkAddressInternal(addrStr: String, cidrBlock: Int): Boolean = {
        val ip = IPv4(addrStr)
        val netAddr = ip.mask(cidrBlock)
        ip == netAddr
    }

    protected def longToIPv4(ip: Long): IPv4 = IPv4((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
    protected def IPv4ToLong(ip: String): Long = {
        val fragments = ip.split('.')
        require(fragments.length == 4, "Bad IPv4 address.")
        var ipNum = 0L
        for (i <- fragments.indices) {
            val frag2Num = fragments(i).toInt
            require(!(fragments(i).length > 1 && frag2Num == 0), "Bad IPv4 address.")
            require(frag2Num >= 0 && frag2Num <= 255, "Bad IPv4 address.")
            ipNum = frag2Num | ipNum << 8L
        }
        ipNum
    }
}

trait IPv6Traits {
    protected def generateZeroesStart(num: Int): String = {
        var zeros = ""
        for (i <- 0 to num) zeros += "0:"
        zeros
    }
    protected def generateZeroesEnds(num: Int): String = {
        var zeros = ""
        for (i <- 0 to num) zeros += ":0"
        zeros
    }
    protected def generateZeroesMiddle(num: Int): String = {
        var zeros = ""
        for (i <- 0 until num) {
            zeros += ":0"
        }
        zeros += ":"
        zeros
    }

    protected def expandIPv6Internal(ip: String): Array[String] = {
        if (!ip.contains("::")) return ip.split(':')
        else if (ip=="::") return "0:0:0:0:0:0:0:0".split(':')

        val numOfColons = ip.count(_ == ':')

        if (ip.startsWith("::")) ip.replace("::", generateZeroesStart(8 - numOfColons)).split(':')
        else if (ip.endsWith("::")) ip.replace("::", generateZeroesEnds(8 - numOfColons)).split(':')
        else ip.replace("::", generateZeroesMiddle(8 - numOfColons)).split(':')
    }

    protected def expandIPv6(ip: String): String = {
        val expandedIPv6 = expandIPv6Internal(ip)
        s"${expandedIPv6(0)}:${expandedIPv6(1)}:${expandedIPv6(2)}:${expandedIPv6(3)}:${expandedIPv6(4)}:${expandedIPv6(5)}:${expandedIPv6(6)}:${expandedIPv6(7)}"
    }

    protected def IPv6ToBigInt(ip: String): BigInt = {
        val fragments = expandIPv6Internal(ip)
        require(fragments.length == 8, "Bad IPv6.")
        var ipNum = BigInt("0")
        for (i <-fragments.indices) {
            require(fragments(i).length <= 4, "Bad IPv6.")
            val frag2Long =  Integer.parseInt(fragments(i), 16)
            ipNum = frag2Long | ipNum << 16
        }
        ipNum
    }

    protected def bigIntToIPv6(ip: BigInt): IPv6 = IPv6((for(a<-7 to 0 by -1) yield ((ip>>(a*16))&0xffff).toString(16)).mkString(":"))

}