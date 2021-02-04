package com.databricks115

import org.scalatest.FunSuite

class TestIPAddress extends FunSuite with SparkSessionTestWrapper{
    def longToIP(ip:Long): String = (for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString(".")

    /*
    Multicast range:
      224.0.0.0 to 239.255.255.255
      3758096384 to 4026531839
    */
    test("Multicast Addresses") {
        //first and last multicast IPs
        val multicastIPs = List(
            new IPAddress("224.0.0.0"),
            new IPAddress("239.255.255.255")
        )

        //first-1 and last+1 multicast IPs
        val notMulticastIPs = List(
            new IPAddress("223.225.225.225"),
            new IPAddress("240.0.0.0")
        )

        assert(multicastIPs.forall(ip => ip.isMulticast))
        assert(notMulticastIPs.forall(ip => !ip.isMulticast))
    }

    /*
    Private range:
      1)
        10.0.0.0 to 10.255.255.255
        167772160 to 184549375
      2)
        172.16.0.0 to 172.31.255.255
        2886729728 to 2887778303
      3)
        192.168.0.0 to 192.168.255.255
        3232235520 to 3232301055
    */
    test("Private Addresses") {
        //first and last of all private IPs
        val privateIPs = List(
            new IPAddress("10.0.0.0"),
            new IPAddress("10.255.255.255"),

            new IPAddress("172.16.0.0"),
            new IPAddress("172.31.255.255"),

            new IPAddress("192.168.0.0"),
            new IPAddress("192.168.255.255")
        )

        //first-1 and last+1 of all private IPs
        val notPrivateIPs = List(
            new IPAddress("9.255.255.255"),
            new IPAddress("11.0.0.0"),

            new IPAddress("172.15.255.255"),
            new IPAddress("172.32.0.0"),

            new IPAddress("192.167.255.255"),
            new IPAddress("192.169.0.0")
        )

        assert(privateIPs.forall(ip => ip.isPrivate))
        assert(notPrivateIPs.forall(ip => !ip.isPrivate))
    }

    /*
    Site Local range:
      //Same as private
  */
    test("Site Local Addresses") {
        //first and last of all private IPs
        val siteLocalIPs = List(
            new IPAddress("10.0.0.0"),
            new IPAddress("10.255.255.255"),

            new IPAddress("172.16.0.0"),
            new IPAddress("172.31.255.255"),

            new IPAddress("192.168.0.0"),
            new IPAddress("192.168.255.255")
        )

        //first-1 and last+1 of all private IPs
        val notSiteLocalIPs = List(
            new IPAddress("9.255.255.255"),
            new IPAddress("11.0.0.0"),

            new IPAddress("172.15.255.255"),
            new IPAddress("172.32.0.0"),

            new IPAddress("192.167.255.255"),
            new IPAddress("192.169.0.0")
        )

        assert(siteLocalIPs.forall(ip => ip.isSiteLocal))
        assert(notSiteLocalIPs.forall(ip => !ip.isSiteLocal))
    }

    /*
    Global range:
        Everything that's not private
  */
    test("Global Addresses") {
        //first and last of all private IPs
        val globalIPs = List(
            new IPAddress("10.0.0.0"),
            new IPAddress("10.255.255.255"),

            new IPAddress("172.16.0.0"),
            new IPAddress("172.31.255.255"),

            new IPAddress("192.168.0.0"),
            new IPAddress("192.168.255.255")
        )

        //first-1 and last+1 of all private IPs
        val notGlobalIPs = List(
            new IPAddress("9.255.255.255"),
            new IPAddress("11.0.0.0"),

            new IPAddress("172.15.255.255"),
            new IPAddress("172.32.0.0"),

            new IPAddress("192.167.255.255"),
            new IPAddress("192.169.0.0")
        )

        //tests opposite of private
        assert(globalIPs.forall(ip => !ip.isGlobal))
        assert(notGlobalIPs.forall(ip => ip.isGlobal))
    }

    /*
    Unspecified range:
      0.0.0.0
      0
  */
    test("Unspecified Addresses") {
        val unspecifiedIP = new IPAddress("0.0.0.0")
        val specifiedIP = new IPAddress("0.0.0.1")

        assert(unspecifiedIP.isUnspecified)
        assert(!specifiedIP.isUnspecified)
    }

    /*
     Loopback range:
      127.0.0.0 to 127.255.255.255
      2130706432 to 2147483647
  */
    test("Loopback Addresses") {
        //first and last loopback IPs
        val loopbackIPs = List(
            new IPAddress("127.0.0.0"),
            new IPAddress("127.255.255.255")
        )

        //first-1 and last+1 loopback IPs
        val notLoopbackIPs = List(
            new IPAddress("126.255.255.255"),
            new IPAddress("128.0.0.0")
        )

        assert(loopbackIPs.forall(ip => ip.isLoopback))
        assert(notLoopbackIPs.forall(ip => !ip.isLoopback))
    }

    /*
    Link Local range:
      169.254.0.0 to 169.254.255.255
      2851995648 to 2852061183
  */
    test("Link Local Addresses") {
        //first and last link local IPs
        val linkLocalIPs = List(
            new IPAddress("169.254.0.0"),
            new IPAddress("169.254.255.255")
        )

        //first-1 and last+1 link local IPs
        val notLinkLocalIPs = List(
            new IPAddress("169.253.255.255"),
            new IPAddress("169.255.0.0")
        )

        assert(linkLocalIPs.forall(ip => ip.isLinkLocal))
        assert(notLinkLocalIPs.forall(ip => !ip.isLinkLocal))
    }

    /*
    Reserved range: 🤮
      1)
        0.0.0.0 to 0.255.255.255
        0 to 16777215
      2)
        Private
      3)
        100.64.0.0 to 100.127.255.255
        1681915904 to 1686110207
      4)
        Loopback
      5)
        Link Local
      6)
        192.0.0.0 to 192.0.0.255
        3221225472 to 3221225727
      7)
        192.0.2.0 to 192.0.2.255
        3221225984 to 3221226239
      8)
        192.88.99.0 to 192.88.99.255
        3227017984 to 3227018239
      9)
        198.18.0.0 to 198.19.255.255
        3323068416 to 3323199487
      10)
        198.51.100.0 to 198.51.100.255
        3325256704 to 3325256959
      11)
        203.0.113.0 to 203.0.113.255
        3405803776 to 3405804031
      12)
        Multicast
      13)
        240.0.0.0 to 255.255.255.255
        4026531840 to 4294967295
  */
    test("Reserved Addresses") {
        //first and last of all reserved IPs
        val reservedIPs = List(
            new IPAddress("0.0.0.0"),
            new IPAddress("0.255.255.255"),

            new IPAddress("10.0.0.0"),
            new IPAddress("10.255.255.255"),

            new IPAddress("100.64.0.0"),
            new IPAddress("100.127.255.255"),

            new IPAddress("127.0.0.0"),
            new IPAddress("127.255.255.255"),

            new IPAddress("169.254.0.0"),
            new IPAddress("169.254.255.255"),

            new IPAddress("192.0.0.0"),
            new IPAddress("192.0.0.255"),

            new IPAddress("192.0.2.0"),
            new IPAddress("192.0.2.255"),

            new IPAddress("192.88.99.0"),
            new IPAddress("192.88.99.255"),

            new IPAddress("198.18.0.0"),
            new IPAddress("198.19.255.255"),

            new IPAddress("198.51.100.0"),
            new IPAddress("198.51.100.255"),

            new IPAddress("203.0.113.0"),
            new IPAddress("203.0.113.255"),

            new IPAddress("224.0.0.0"),
            new IPAddress("239.255.255.255"),

            new IPAddress("240.0.0.0"),
            new IPAddress("255.255.255.255")
        )

        //first-1 and last+1 of all reserved IPs
        val notReservedIPs = List(
            new IPAddress("1.0.0.0"),

            new IPAddress("9.255.255.255"),
            new IPAddress("11.0.0.0"),

            new IPAddress("100.63.255.255"),
            new IPAddress("100.128.0.0"),

            new IPAddress("126.255.255.255"),
            new IPAddress("128.0.0.0"),

            new IPAddress("169.253.255.255"),
            new IPAddress("169.255.0.0"),

            new IPAddress("191.255.255.255"),
            new IPAddress("192.0.1.0"),

            new IPAddress("192.0.1.255"),
            new IPAddress("192.0.3.0"),

            new IPAddress("192.88.98.255"),
            new IPAddress("192.88.100.0"),

            new IPAddress("198.17.255.255"),
            new IPAddress("198.20.0.0"),

            new IPAddress("198.51.99.255"),
            new IPAddress("198.51.101.0"),

            new IPAddress("203.0.112.255"),
            new IPAddress("203.0.114.0"),

            new IPAddress("223.225.225.225")
        )

        assert(reservedIPs.forall(ip => ip.isReserved))
        assert(notReservedIPs.forall(ip => !ip.isReserved))
    }

    test("isIP - valid IP") {
        var ip = new IPAddress("192.168.0.1")
        assert(ip.isIP)
    }

    test("isIP - invalid IP") {
        var ip = new IPAddress("192.168.0")
        assert(!ip.isIP)
    }

}
