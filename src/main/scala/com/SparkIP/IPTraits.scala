package com.SparkIP

trait IPv4Traits {
    protected def IPv4SubnetToCIDR(subnet: String): Int = {
        try {
            subnet.split('.').map(i => i.toInt.toBinaryString.count(_=='1')).sum
        } catch {
            case _: Throwable => throw new Exception("Can only use dotted decimal CIDR on IPv4.")
        }
    }
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
        require(num >= 0, "Can only use :: for 2 or more 0's.")
        "0:"*num
    }
    protected def generateZeroesEnd(num: Int): String = {
        require(num >= 1, "Can only use :: for 2 or more 0's.")
        ":0"*num
    }
    protected def expandIPv6Internal(ip: String): Array[String] = {
        if (!ip.contains("::")) return ip.split(':')
        else if (ip=="::") return Array.fill(8)("0")

        val numOfColons = ip.count(_ == ':')

        if (ip.startsWith("::")) ip.replace("::", generateZeroesStart(9 - numOfColons)).split(':')
        else if (ip.endsWith("::")) ip.replace("::", generateZeroesEnd(9 - numOfColons)).split(':')
        else ip.replace("::", s"${generateZeroesEnd(8 - numOfColons)}:").split(':')
    }
    protected def IPv6ToBigInt(ip: String): BigInt = {
        val fragments = expandIPv6Internal(ip)
        require(fragments.length == 8, "Bad IPv6.")

        fragments.foldLeft(BigInt("0"))((i, j) => {
            require(j.length <= 4, "Bad IPv6.")
            Integer.parseInt(j, 16) | i << 16
        })
    }
}

trait IPTraits extends IPv4Traits with IPv6Traits {
    protected def IPToNum(ip: String): Either[Long, BigInt] = {
        val v4 = {
            try {
                Some(IPv4ToLong(ip))
            }
            catch {
                case _: Throwable => None
            }
        }
        lazy val v6 = {
            try {
                Some(IPv6ToBigInt(ip))
            }
            catch {
                case _: Throwable => None
            }
        }

        if (v4.isDefined) Left(v4.get)
        else if (v6.isDefined) Right(v6.get)
        else throw new Exception("Bad IP address.")
    }

    protected def numToIP(ip: Long): IPAddress =
        IPAddress((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
    protected def numToIP(ip: BigInt): IPAddress =
        IPAddress((for(a<-7 to 0 by -1) yield ((ip>>(a*16))&0xffff).toString(16)).mkString(":"))

    // Checks whether a IP address is the network address of this network
    protected def isNetworkAddress(addrStr: String, cidrBlock: Int): Boolean = {
        val ip = IPAddress(addrStr)
        ip == ip.mask(cidrBlock)
    }
}