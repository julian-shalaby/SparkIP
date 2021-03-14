package com.databricks115

case class IPv4(IPAddress: String) extends sharedIPTraits with Ordered[IPv4] with IPv4Traits {
    //IPv4 as a number
    val addrL: Long = IPv4ToLong(IPAddress)
    require(isIP(IPAddress), "IPv4 invalid.")

    //compare operations
    override def <(that: IPv4): Boolean = this.addrL < that.addrL
    override def >(that: IPv4): Boolean = this.addrL > that.addrL
    override def <=(that: IPv4): Boolean = this.addrL <= that.addrL
    override def >=(that: IPv4): Boolean = this.addrL >= that.addrL
    def compare(that: IPv4): Int = (this.addrL - that.addrL).toInt

    //Return network address of IP address
    def mask(maskIP: Int): IPv4 = {
        require(maskIP >= 0 && maskIP <= 32, "Can only mask 0-32.")
        longToIPv4(0xFFFFFFFF << (32 - maskIP) & addrL)
    }
    def mask(maskIP: String): IPv4 = {
        require(isIP(maskIP), "IPv4 invalid.")
        longToIPv4(IPv4ToLong(maskIP) & addrL)
    }

    //converts IPv4 address to IPv4 network
    def toNetwork: IPv4Network = IPv4Network(IPAddress)

    // Address Types
    lazy val isMulticast: Boolean = if (addrL >= 3758096384L && addrL <= 4026531839L) true else false
    lazy val isPrivate: Boolean = if (
        (addrL >= 167772160L && addrL <= 184549375L) ||
          (addrL >= 2886729728L && addrL <= 2887778303L) ||
          (addrL >= 3232235520L && addrL <= 3232301055L)
    ) true else false
    lazy val isGlobal: Boolean = !isPrivate
    lazy val isUnspecified: Boolean = if (addrL == 0) true else false
    lazy val isLoopback: Boolean = if (addrL >= 2130706432L && addrL <= 2147483647L) true else false
    lazy val isLinkLocal: Boolean = if (addrL >= 2851995648L && addrL <= 2852061183L) true else false
    lazy val isReserved: Boolean = if (
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

    def sixToFour: IPv6 = IPv6(s"2002:${IPv4to2IPv6Octets(this)}:0:0:0:0:0")
    def IPv4Mapped: IPv6 = IPv6(s"0:0:0:0:0:ffff:${IPv4to2IPv6Octets(this)}")
    def teredo: IPv6 = IPv6(s"2001:0:${IPv4to2IPv6Octets(this)}:0:0:0:0")
}