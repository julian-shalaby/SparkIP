/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.types

import org.apache.spark.SparkFunSuite

class TestIPv4Type extends SparkFunSuite {
//
//    test("Mask IP String - success") {
//        val maskTest = IPv4Type("212.222.131.201")
//        val maskTest2 = IPv4Type("212.222.128.0")
//        assert(maskTest.mask("255.255.248.0") == maskTest2)
//    }
//
//    test("Mask IP String - failure") {
//        val maskTest = IPv4Type("212.222.131.201")
//        val maskTest2 = IPv4Type("212.222.1.0")
//        assert(maskTest.mask("255.255.248.0") != maskTest2)
//    }
//
//    test("Mask IP Int - success") {
//        val maskTest = IPv4Type("212.222.131.201")
//        val maskTest2 = IPv4Type("212.222.128.0")
//        assert(maskTest.mask(18) == maskTest2)
//    }
//
//    test("Mask IP Int - failure") {
//        val maskTest = IPv4Type("212.222.131.201")
//        val maskTest2 = IPv4Type("212.222.1.0")
//        assert(maskTest.mask(18) != maskTest2)
//    }
//
//    /*
//    Multicast range:
//      224.0.0.0 to 239.255.255.255
//      3758096384 to 4026531839
//    */
//    test("Multicast Addresses") {
//        //first and last multicast IPs
//        val MulticastIPs = List(IPv4Type("224.0.0.0"), IPv4Type("239.255.255.255"))
//        assert(MulticastIPs.forall(ip => ip.isMulticast))
//    }
//    test("Not Multicast Addresses") {
//        //first-1 and last+1 multicast IPs
//        val notMulticastIPs = List(IPv4Type("223.225.225.225"), IPv4Type("240.0.0.0"))
//        assert(notMulticastIPs.forall(ip => !ip.isMulticast))
//    }
//
//    /*
//    Private range:
//      1)
//        10.0.0.0 to 10.255.255.255
//        167772160 to 184549375
//      2)
//        172.16.0.0 to 172.31.255.255
//        2886729728 to 2887778303
//      3)
//        192.168.0.0 to 192.168.255.255
//        3232235520 to 3232301055
//    */
//    test("Private Addresses") {
//        //first and last of all private IPs
//        val privateIPs = List(
//            IPv4Type("10.0.0.0"),
//            IPv4Type("10.255.255.255"),
//
//            IPv4Type("172.16.0.0"),
//            IPv4Type("172.31.255.255"),
//
//            IPv4Type("192.168.0.0"),
//            IPv4Type("192.168.255.255")
//        )
//        assert(privateIPs.forall(ip => ip.isPrivate))
//    }
//    test("Not Private Addresses") {
//        //first-1 and last+1 of all private IPs
//        val notPrivateIPs = List(
//            IPv4Type("9.255.255.255"),
//            IPv4Type("11.0.0.0"),
//
//            IPv4Type("172.15.255.255"),
//            IPv4Type("172.32.0.0"),
//
//            IPv4Type("192.167.255.255"),
//            IPv4Type("192.169.0.0")
//        )
//        assert(notPrivateIPs.forall(ip => !ip.isPrivate))
//    }
//
//    /*
//    Global range:
//        Everything that's not private
//  */
//    test("Global Addresses") {
//        //first and last of all private IPs
//        val globalIPs = List(
//            IPv4Type("10.0.0.0"),
//            IPv4Type("10.255.255.255"),
//
//            IPv4Type("172.16.0.0"),
//            IPv4Type("172.31.255.255"),
//
//            IPv4Type("192.168.0.0"),
//            IPv4Type("192.168.255.255")
//        )
//        //tests opposite of private
//        assert(globalIPs.forall(ip => !ip.isGlobal))
//    }
//    test("Not Global Addresses") {
//        //first-1 and last+1 of all private IPs
//        val notGlobalIPs = List(
//            IPv4Type("9.255.255.255"),
//            IPv4Type("11.0.0.0"),
//
//            IPv4Type("172.15.255.255"),
//            IPv4Type("172.32.0.0"),
//
//            IPv4Type("192.167.255.255"),
//            IPv4Type("192.169.0.0")
//        )
//        //tests opposite of private
//        assert(notGlobalIPs.forall(ip => ip.isGlobal))
//    }
//
//    /*
//    Unspecified range:
//      0.0.0.0
//      0
//  */
//    test("Unspecified Address") {
//        val unspecifiedIP = IPv4Type("0.0.0.0")
//        assert(unspecifiedIP.isUnspecified)
//    }
//    test("Not Unspecified Address") {
//        val specifiedIP = IPv4Type("0.0.0.1")
//        assert(!specifiedIP.isUnspecified)
//    }
//
//    /*
//     Loopback range:
//      127.0.0.0 to 127.255.255.255
//      2130706432 to 2147483647
//  */
//    test("Loopback Addresses") {
//        //first and last loopback IPs
//        val loopbackIPs = List(
//            IPv4Type("127.0.0.0"),
//            IPv4Type("127.255.255.255")
//        )
//        assert(loopbackIPs.forall(ip => ip.isLoopback))
//    }
//    test("Not Loopback Addresses") {
//        //first-1 and last+1 loopback IPs
//        val notLoopbackIPs = List(
//            IPv4Type("126.255.255.255"),
//            IPv4Type("128.0.0.0")
//        )
//        assert(notLoopbackIPs.forall(ip => !ip.isLoopback))
//    }
//
//    /*
//    Link Local range:
//      169.254.0.0 to 169.254.255.255
//      2851995648 to 2852061183
//  */
//    test("Link Local Addresses") {
//        //first and last link local IPs
//        val linkLocalIPs = List(
//            IPv4Type("169.254.0.0"),
//            IPv4Type("169.254.255.255")
//        )
//        assert(linkLocalIPs.forall(ip => ip.isLinkLocal))
//    }
//    test("Not Link Local Addresses") {
//        //first-1 and last+1 link local IPs
//        val notLinkLocalIPs = List(
//            IPv4Type("169.253.255.255"),
//            IPv4Type("169.255.0.0")
//        )
//        assert(notLinkLocalIPs.forall(ip => !ip.isLinkLocal))
//    }
//
//    /*
//    Reserved range: 🤮
//      1)
//        0.0.0.0 to 0.255.255.255
//        0 to 16777215
//      2)
//        Private
//      3)
//        100.64.0.0 to 100.127.255.255
//        1681915904 to 1686110207
//      4)
//        Loopback
//      5)
//        Link Local
//      6)
//        192.0.0.0 to 192.0.0.255
//        3221225472 to 3221225727
//      7)
//        192.0.2.0 to 192.0.2.255
//        3221225984 to 3221226239
//      8)
//        192.88.99.0 to 192.88.99.255
//        3227017984 to 3227018239
//      9)
//        198.18.0.0 to 198.19.255.255
//        3323068416 to 3323199487
//      10)
//        198.51.100.0 to 198.51.100.255
//        3325256704 to 3325256959
//      11)
//        203.0.113.0 to 203.0.113.255
//        3405803776 to 3405804031
//      12)
//        Multicast
//      13)
//        240.0.0.0 to 255.255.255.255
//        4026531840 to 4294967295
//  */
//    test("Reserved Addresses") {
//        //first and last of all reserved IPs
//        val reservedIPs = List(
//            IPv4Type("0.0.0.0"),
//            IPv4Type("0.255.255.255"),
//
//            IPv4Type("10.0.0.0"),
//            IPv4Type("10.255.255.255"),
//
//            IPv4Type("100.64.0.0"),
//            IPv4Type("100.127.255.255"),
//
//            IPv4Type("127.0.0.0"),
//            IPv4Type("127.255.255.255"),
//
//            IPv4Type("169.254.0.0"),
//            IPv4Type("169.254.255.255"),
//
//            IPv4Type("192.0.0.0"),
//            IPv4Type("192.0.0.255"),
//
//            IPv4Type("192.0.2.0"),
//            IPv4Type("192.0.2.255"),
//
//            IPv4Type("192.88.99.0"),
//            IPv4Type("192.88.99.255"),
//
//            IPv4Type("198.18.0.0"),
//            IPv4Type("198.19.255.255"),
//
//            IPv4Type("198.51.100.0"),
//            IPv4Type("198.51.100.255"),
//
//            IPv4Type("203.0.113.0"),
//            IPv4Type("203.0.113.255"),
//
//            IPv4Type("224.0.0.0"),
//            IPv4Type("239.255.255.255"),
//
//            IPv4Type("240.0.0.0"),
//            IPv4Type("255.255.255.255")
//        )
//        assert(reservedIPs.forall(ip => ip.isReserved))
//    }
//    test("Not Reserved Addresses") {
//        //first-1 and last+1 of all reserved IPs
//        val notReservedIPs = List(
//            IPv4Type("1.0.0.0"),
//
//            IPv4Type("9.255.255.255"),
//            IPv4Type("11.0.0.0"),
//
//            IPv4Type("100.63.255.255"),
//            IPv4Type("100.128.0.0"),
//
//            IPv4Type("126.255.255.255"),
//            IPv4Type("128.0.0.0"),
//
//            IPv4Type("169.253.255.255"),
//            IPv4Type("169.255.0.0"),
//
//            IPv4Type("191.255.255.255"),
//            IPv4Type("192.0.1.0"),
//
//            IPv4Type("192.0.1.255"),
//            IPv4Type("192.0.3.0"),
//
//            IPv4Type("192.88.98.255"),
//            IPv4Type("192.88.100.0"),
//
//            IPv4Type("198.17.255.255"),
//            IPv4Type("198.20.0.0"),
//
//            IPv4Type("198.51.99.255"),
//            IPv4Type("198.51.101.0"),
//
//            IPv4Type("203.0.112.255"),
//            IPv4Type("203.0.114.0"),
//
//            IPv4Type("223.225.225.225")
//        )
//        assert(notReservedIPs.forall(ip => !ip.isReserved))
//    }
//
//    test("== - success") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(ip1 == ip2)
//    }
//
//    test("== - failure") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(ip1 != ip2)
//    }
//
//    test("< - success") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(ip1 == ip2)
//    }
//
//    test("< - failure 1") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(!(ip1 < ip2))
//    }
//
//    test("< - failure 2") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(!(ip1 < ip2))
//    }
//
//    test("> - success") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(ip1 > ip2)
//    }
//
//    test("> - failure 1") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(!(ip1 > ip2))
//    }
//
//    test("> - failure 2") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(!(ip1 > ip2))
//    }
//
//    test("<= - success") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(ip1 <= ip2)
//    }
//
//    test("<= - failure") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(!(ip1 <= ip2))
//    }
//
//
//    test(">= - success") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert(ip1 >= ip2)
//    }
//
//    test(">= - failure 1") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert(!(ip1 >= ip2))
//    }
//
//    test("compareTo - success 1") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert((ip1 compareTo ip2) == 1)
//    }
//
//    test("compareTo - success 2") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert((ip1 compareTo ip2) == -1)
//    }
//
//    test("compare - success 1") {
//        var ip1 = IPv4Type("192.168.0.1")
//        var ip2 = IPv4Type("192.168.0.0")
//        assert((ip1 compare ip2) == 1)
//    }
//
//    test("compare - success 2") {
//        var ip1 = IPv4Type("192.168.0.0")
//        var ip2 = IPv4Type("192.168.0.1")
//        assert((ip1 compare ip2) == -1)
//    }
}
