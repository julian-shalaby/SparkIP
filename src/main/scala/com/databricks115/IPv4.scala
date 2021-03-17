package com.databricks115
import java.math.BigInteger
/*
    ToDO:
        1) Sort IPv4 list in order like https://github.com/risksense/ipaddr#ipaddress
        2) Make sure mask with string is a valid mask (only a network addr)
 */
case class IPv4(ipAddress: String) extends Ordered[IPv4] with IPv4Traits {
    //IPv4 as a number
    val addrL: Long = IPv4ToLong(ipAddress)

    //compare operations
    override def <(that: IPv4): Boolean = this.addrL < that.addrL
    override def >(that: IPv4): Boolean = this.addrL > that.addrL
    override def <=(that: IPv4): Boolean = this.addrL <= that.addrL
    override def >=(that: IPv4): Boolean = this.addrL >= that.addrL
    def ==(that: IPv4): Boolean = this.addrL == that.addrL
    def !=(that: IPv4): Boolean = this.addrL != that.addrL
    def compare(that: IPv4): Int = (this.addrL - that.addrL).toInt

    //Return network address of IP address
    def mask(maskIP: Int): IPv4 = {
        require(maskIP >= 0 && maskIP <= 32, "Can only mask 0-32.")
        longToIPv4(0xFFFFFFFF << (32 - maskIP) & addrL)
    }
    def mask(maskIP: String): IPv4 = {
        longToIPv4(IPv4ToLong(maskIP) & addrL)
    }

    // Address Types
    lazy val isMulticast: Boolean = addrL >= 3758096384L && addrL <= 4026531839L
    lazy val isPrivate: Boolean = (addrL >= 167772160L && addrL <= 184549375L) ||
      (addrL >= 2886729728L && addrL <= 2887778303L) ||
      (addrL >= 3232235520L && addrL <= 3232301055L)
    lazy val isGlobal: Boolean = !isPrivate
    lazy val isUnspecified: Boolean = addrL == 0
    lazy val isLoopback: Boolean = addrL >= 2130706432L && addrL <= 2147483647L
    lazy val isLinkLocal: Boolean = addrL >= 2851995648L && addrL <= 2852061183L
    lazy val isReserved: Boolean = (addrL >= 0L && addrL <= 16777215L) ||
      isPrivate ||
      (addrL >= 1681915904L && addrL <= 1686110207L) ||
      isLoopback || isLinkLocal ||
      (addrL >= 3221225472L && addrL <= 3221225727L) ||
      (addrL >= 3221225984L && addrL <= 3221226239L) ||
      (addrL >= 3227017984L && addrL <= 3227018239L) ||
      (addrL >= 3323068416L && addrL <= 3323199487L) ||
      (addrL >= 3325256704L && addrL <= 3325256959L) ||
      (addrL >= 3405803776L && addrL <= 3405804031L) ||
      isMulticast ||
      (addrL >= 4026531840L && addrL <= 4294967294L) ||
      (addrL == 4294967295L)

    //interface with ipv6
    private def IPv4to2IPv6Octets(ip: IPv4): String = s"${(ip.addrL >> 16 & 0xFFFF).toHexString}:${(ip.addrL & 0xFFFF).toHexString}"
    def sixToFour: IPv6 = IPv6(s"2002:${IPv4to2IPv6Octets(this)}:0:0:0:0:0")
    def sixToFour(subnet: String, interfaceID: String): IPv6 = IPv6(s"2002:${IPv4to2IPv6Octets(this)}:$subnet:$interfaceID")
    def IPv4Mapped: IPv6 = IPv6(s"0:0:0:0:0:ffff:${IPv4to2IPv6Octets(this)}")
    def teredo: IPv6 = IPv6(s"2001:0:${IPv4to2IPv6Octets(this)}:0:0:0:0")
    def teredo(flags: String, udpPort: String, clientIPv4: String): IPv6 =
        IPv6(s"2001:0:${IPv4to2IPv6Octets(this)}:$flags:$udpPort:$clientIPv4")
    def teredo(flags: String, udpPort: String, clientIPv4: IPv4): IPv6 = {
        def IPv4XorTo2IPv6Octets: String = {
            val xord = new BigInteger(s"${IPv4ToLong(clientIPv4.ipAddress)}").xor(new BigInteger("4294967295"))
            s"${xord.shiftRight(16).toString(16)}:${xord.and(new BigInteger("65535")).toString(16)}"
        }
        IPv6(s"2001:0:${IPv4to2IPv6Octets(this)}:$flags:$udpPort:$IPv4XorTo2IPv6Octets")
    }
}