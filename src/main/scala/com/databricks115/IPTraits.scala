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

        fragments.foldLeft(0L)((i, j) => {
            val frag2Num = j.toInt
            require(!(j.length > 1 && frag2Num == 0) && (frag2Num >= 0 && frag2Num <= 255), "Bad IPv4 address.")
            frag2Num | i << 8L
        })
    }

}

trait IPv6Traits {
    protected def generateZeroesStart(num: Int): String = {
        require(num >= 1, "Can only use :: for 2 or more 0's.")
        "0:"*(num+1)
    }
    protected def generateZeroesEnd(num: Int): String = {
        require(num >= 1, "Can only use :: for 2 or more 0's.")
        ":0"*(num+1)
    }

    protected def expandIPv6Internal(ip: String): Array[String] = {
        if (!ip.contains("::")) return ip.split(':')
        else if (ip=="::") return "0:0:0:0:0:0:0:0".split(':')

        val numOfColons = ip.count(_ == ':')

        if (ip.startsWith("::")) ip.replace("::", generateZeroesStart(8 - numOfColons)).split(':')
        else if (ip.endsWith("::")) ip.replace("::", generateZeroesEnd(8 - numOfColons)).split(':')
        else ip.replace("::", s"${generateZeroesEnd(7 - numOfColons)}:").split(':')
    }

    protected def IPv6ToBigInt(ip: String): BigInt = {
        val fragments = expandIPv6Internal(ip)
        require(fragments.length == 8, "Bad IPv6.")

        fragments.foldLeft(BigInt("0"))((i, j) => {
            require(j.length <= 4, "Bad IPv6.")
            Integer.parseInt(j, 16) | i << 16
        })
    }

    protected def bigIntToIPv6(ip: BigInt): IPv6 = IPv6((for(a<-7 to 0 by -1) yield ((ip>>(a*16))&0xffff).toString(16)).mkString(":"))

}