package com.databricks115

case class IPv4(addr: String) extends IPAddress with Ordered[IPv4] with IPRegex {
    require(isIP(addr), "IPv4 invalid.")
    
    val addrL: Long = IPv4ToLong(addr)

    //makes sure IP is valid
    override def isIP(ip: String): Boolean = {
        ip match {
            case IPv4Address(o1, o2, o3, o4) => IPv4Validation(List(o1, o2, o3, o4))
            case _ => false
        }
    }

    def isIP(ip: Long): Boolean = ip >= 0L && ip <= 4294967295L

    //compare operations
    override def <(that: IPv4): Boolean = this.addrL < that.addrL
    override def >(that: IPv4): Boolean = this.addrL > that.addrL
    override def <=(that: IPv4): Boolean = this.addrL <= that.addrL
    override def >=(that: IPv4): Boolean = this.addrL >= that.addrL
    //so comparisons between multiple leading 0's will work
    def ==(that: IPv4): Boolean = this.addrL == that.addrL
    override def compareTo(that: IPv4): Int = (this.addrL - that.addrL).toInt
    def compare(that: IPv4): Int = (this.addrL - that.addrL).toInt

    //Return network address of IP address
    def mask(maskIP: Int): IPv4 = {
        require(maskIP >= 1 && maskIP <= 32, "Can only mask 1-32.")
        IPv4(longToIPv4(0xFFFFFFFF << (32 - maskIP) & addrL))
    }
    def mask(maskIP: String): IPv4 = {
        require(isIP(maskIP), "IPv4 invalid.")
        IPv4(longToIPv4(IPv4ToLong(maskIP) & addrL))
    }

    def toNetwork: IPNetwork = IPNetwork(addr)

    // Address Types
    val isMulticast: Boolean = if (addrL >= 3758096384L && addrL <= 4026531839L) true else false
    val isPrivate: Boolean = if (
        (addrL >= 167772160L && addrL <= 184549375L) ||
          (addrL >= 2886729728L && addrL <= 2887778303L) ||
          (addrL >= 3232235520L && addrL <= 3232301055L)
    ) true else false
    val isGlobal: Boolean = !isPrivate
    val isUnspecified: Boolean = if (addrL == 0) true else false
    val isLoopback: Boolean = if (addrL >= 2130706432L && addrL <= 2147483647L) true else false
    val isLinkLocal: Boolean = if (addrL >= 2851995648L && addrL <= 2852061183L) true else false
    val isReserved: Boolean = if (
        (addrL >= 0L && addrL <= 16777215L) ||
          isPrivate ||
          (addrL >= 1681915904L && addrL <= 1686110207L) ||
          isLoopback ||
          isLinkLocal ||
          (addrL >= 3221225472L && addrL <= 3221225727L) ||
          (addrL >= 3221225984L && addrL <= 3221226239L) ||
          (addrL >= 3227017984L && addrL <= 3227018239L) ||
          (addrL >= 3323068416L && addrL <= 3323199487L) ||
          (addrL >= 3325256704L && addrL <= 3325256959L) ||
          (addrL >= 3405803776L && addrL <= 3405804031L) ||
          isMulticast ||
          (addrL >= 4026531840L && addrL <= 4294967294L) ||
          (addrL == 4294967295L)
    ) true else false
}