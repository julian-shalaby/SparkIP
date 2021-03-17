[![license](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://github.com/risksense/ipaddr/blob/master/LICENSE)

# Scala IP Address
IPv4 and IPv6 Network address manipulation library for Scala. Inspired by [Ipaddr](https://github.com/risksense/ipaddr).

## Usage
Add the following to your build.sbt:

`libraryDependencies += "com.risksense" % "ipaddr_2.12" % "1.0.2" //Make and replace with ours` 

## Tutorial
  * [IPv4](#IPv4)
  * [IPv6](#IPv6)
  * [IPv4Network](#IPv4Network)
  * [IPv6Network](#IPv6Network)
  * [IPSet](#IPSet)

## Contributing
Before making any contributions, please check the issues section to make sure your concern is not
duplicate. If your issue is not already addressed, please create one. Describe the problem/bug/feature
that you would like to be solved. If you are ready to contribute, read on...

1. Fork this repo.
2. Create a new branch with a name that hints about what is added/fixed in the branch.
3. Squash your commits. (We like having single commit pull requests)
4. Open a pull request and make sure your commit message references the issue number.

## License
This project is licensed under the Apache License. Please see [LICENSE](LICENSE) file for more details.

## Authors
  * [Julian Shalaby](https://github.com/jshalaby510) - (List our work?)
  * [You](https://github.com/{yourUsername}) - (List our work?)

### <a name="IPv4"></a>IPv4
Create an `IPv4`
```scala
val ip = IPv4("192.0.0.0")
```

**Compare addresses**
```scala
val ip1 = IPv4("0.0.0.0")
val ip2 = IPv4("255.255.255.255")

ip1 == ip2 // false
ip1 != ip2 // true
ip1 < ip2 // true
ip1 > ip2 // false
ip1 <= ip2 // true
ip1 >= ip2 // false
```

**String and numerical value of the address**
```scala
val ip = IPv4("192.0.0.0")

ip.ipAddress // 192.0.0.0
ip.addrL // 3221225472
```

**Sorting**
```scala
// We need to implement
```

**Categorization**
  * Private
```scala
IPv4("192.168.0.1").isPrivate // true
IPv4("8.8.8.8").isPrivate // false
```
  * LinkLocal
```scala
IPv4("169.254.0.0").isLinkLocal // true
IPv4("192.168.0.1").isLinkLocal  // false
```
  * Loopback
```scala
IPv4("127.0.0.1").isLoopback // true
IPv4("192.168.0.1").isLoopback  // false
```
  * Multicast
```scala
IPv4("239.192.0.1").isMulticast // true
IPv4("192.168.0.1").isMulticast  // false
```
  * Reserved
```scala
IPv4("192.0.0.1").isReserved // true
IPv4("10.1.2.0").isReserved  // false
```
  * Unspecified
```scala
IPv4("0.0.0.0").isUnspecified // true
IPv4("10.1.2.0").isUnspecified  // false
```

**IPv6 interfacing**
* sixToFour
```scala
IPv4("73.231.169.178").sixToFour // IPv6(2002:49e7:a9b2:0:0:0:0:0)
IPv4("12.155.166.101").sixToFour("1", "0000:0000:0C9B:A665") // IPv6(2002:c9b:a665:1:0000:0000:0C9B:A665)
```

* IPv4Mapped
```scala
IPv4("73.231.169.178").IPv4Mapped // IPv6(0:0:0:0:0:ffff:49e7:a9b2)
```

* teredo
```scala
IPv4("73.231.169.178").teredo // IPv6(2001:0:49e7:a9b2:0:0:0:0)
IPv4("65.54.227.120").teredo("8000", "63BF", "3FFF:FDD2") // IPv6("2001:0000:4136:E378:8000:63BF:3FFF:FDD2")
IPv4("65.54.227.120").teredo("8000", "63BF", IPv4("192.0.2.45")) // IPv6("2001:0000:4136:E378:8000:63BF:3FFF:FDD2")
```

**Masking**
* Mask
```scala
IPv4("73.231.169.178").mask(16) // IPv4(192.23.0.0)
IPv4("73.231.169.178").mask("255.255.0.0") // IPv4(192.23.0.0)
```

### <a name="IPv6"></a>IPv6 (NOTE: Only full address format is currently fully supported)
Create an `IPv6`
```scala
val ip = IPv6("2001:0:0:0:0:0:0:0")
val ip2 = IPv6("2001::")
val ip3 = IPv6("::2001")
val ip4 = IPv6("2001::2001")
```

**Compare addresses**
```scala
val ip1 = IPv6("2001::")
val ip2 = IPv6("::2001")

ip1 == ip2 // false
ip1 != ip2 // true
ip1 < ip2 // false
ip1 > ip2 // true
ip1 <= ip2 // false
ip1 >= ip2 // true
```

**String and numerical value of the address**
```scala
val ip = IPv6("2001::")

ip.ipAddress // 2001::
ip.addrBI // 42540488161975842760550356425300246528
```

**Sorting**
```scala
// We need to implement
```

**Categorization**
  * LinkLocal
```scala
IPv6("febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff").isLinkLocal // true
IPv4("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff").isLinkLocal  // false
```
  * Loopback
```scala
IPv6("::1").isLoopback // true
IPv6("1::1").isLoopback  // false
```
  * Multicast
```scala
IPv6("ff00::").isMulticast // true
IPv6("fe00::").isMulticast  // false
```
  * Reserved
```scala
IPv6("ff00::").isReserved // true
IPv6("fe00::").isReserved  // false
```
  * Unspecified
```scala
IPv6("::").isUnspecified // true
IPv6("1::").isUnspecified  // false
```

  * UniqueLocal
```scala
IPv6("fdff:ffff:ffff:ffff:ffff:ffff:ffff:ffff").isUniqueLocal // true
IPv6("1::").isUniqueLocal  // false
```

 * IPv4Mapped
```scala
IPv6("::ffff:49e7:a9b2").isIPv4Mapped // true
IPv6("::").isIPv4Mapped  // false
```

 * IPv4Translated
```scala
IPv6("::ffff:0000:0000:0000").isIPv4Translated // true
IPv6("::fff:0:0:0:0:0").isIPv4Translated  // false
```

 * IPv4IPv6Translated
```scala
IPv6("64:ff9b::").isIPv4IPv6Translated // true
IPv6("::").isIPv4IPv6Translated  // false
```

 * Teredo
```scala
IPv6("2001::").isTeredo // true
IPv6("::").isTeredo  // false
```

 * 6to4
```scala
IPv6("2002::").is6to4 // true
IPv6("::").is6to4  // false
```

**IPv4 interfacing**
* sixToFour
```scala
IPv6("2002:49e7::").sixToFour // IPv4("73.231.169.178")
```

* IPv4Mapped
```scala
IPv6("::ffff:49e7:a9b2").IPv4Mapped // IPv4("73.231.169.178")
```

* teredo server
```scala
IPv6("2001:0:49e7:a9b2::").teredoServer // IPv4("73.231.169.178")
```

* teredo client
```scala
IPv6("2001:0000:4136:e378:8000:63bf:3fff:fdd2").teredoClient // IPv4("192.0.2.45")
```

**Masking**
* Mask
```scala
IPv6("2001:db8:3333:4444:5555:6666:7777:8888").mask(32) // IPv6(2001:db8::)
```

### <a name="IPv4Network"></a>IPv4Network
**Create an `IPv4Network` from CIDR, dotted decimal, verbose dotted decimal, or range notations**
```scala
val net1 = IPv4Network("192.0.0.0/16")
val net2 = IPv4Network("192.0.0.0/255.255.0.0")
val net3 = IPv4Network("Address 192.0.0.0 Network 255.255.0.0")
val net4 = IPv4Network("192.0.0.0-255.0.0.0")
```

**Details about the network**
```scala
val net = IPv4Network("192.0.0.0/16")

net.ipNetwork // 192.0.0.0/16
net.addrLStart // 3221225472
net.addrLEnd // 3221291007
net.range // IPv4(192.0.0.0)-IPv4(192.0.255.255)
net.networkAddress // IPv4(192.0.0.0)
net.broadcastAddress // IPv4(192.0.255.255)
```

**Check if an IP address is in the network**
```scala
val net = IPv4Network("192.0.0.0/16")

net.contains("192.0.0.136") // true
net.contains("255.0.0.136") // false
```

**Check if an IP network intersects this network**
```scala
val net1 = IPv4Network("192.0.0.0/16")
val net2 = IPv4Network("192.0.0.0/18")
val net3 = IPv4Network("0.0.0.0/16")

net1.netsIntersect(net2) // true
net.netsIntersect(net3) // false
```

**Sorting**
```scala
// We need to implement
```

**Compare networks**
```scala
val net1 = IPv4Network("0.0.0.0/16")
val net2 = IPv4Network("192.0.0.0/16")

net1 == net2 // false
net1 != net2 // true
net1 < net2 // true
net1 > net2 // false
net1 <= net2 // true
net1 >= net2 // false
```

### <a name="IPv6Network"></a>IPv6Network
**Create an `IPv6Network` from CIDR or range notations**
```scala
val net1 = IPv6Network("2001::/16")
val net2 = IPv6Network("2001::-ffff::")
```

**Details about the network**
```scala
val net = IPv6Network("2001::/16")

net.ipNetwork // 2001::/16
net.addrBIStart // 42540488161975842760550356425300246528
net.addrBIEnd // 42545680458834377588178886921629466623
net.range // IPv6(2001::)-IPv6(2001:ffff:ffff:ffff:ffff:ffff:ffff:ffff)
net.networkAddress // IPv6(2001::)
net.broadcastAddress // IPv6(2001:ffff:ffff:ffff:ffff:ffff:ffff:ffff)
```

**Check if an IP address is in the network**
```scala
val net = IPv6Network("2001::/16")

net.contains("2001::ffff") // true
net.contains("::") // false
```

**Check if an IP network intersects this network**
```scala
val net1 = IPv6Network("2001:0db8:85a3:0000::/64")
val net2 = IPv6Network("2001::/16")
val net3 = IPv6Network("::/16")

net1.netsIntersect(net2) // true
net.netsIntersect(net3) // false
```

**Sorting**
```scala
// We need to implement
```

**Compare networks**
```scala
val net1 = IPv6Network("::/16")
val net2 = IPv6Network("2001::/16")

net1 == net2 // false
net1 != net2 // true
net1 < net2 // true
net1 > net2 // false
net1 <= net2 // true
net1 >= net2 // false
```

### <a name="IPSet"></a>IPSet
**I'm too lazy rn**