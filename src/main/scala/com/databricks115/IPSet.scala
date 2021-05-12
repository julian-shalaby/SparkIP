package com.databricks115

case class IPSet(ipAddresses: Any*) {
    def this() = this(null)
    var ipMap: Map[String, BigInt] = Map()
    private def initializeMap(): Unit = {
        ipAddresses.foreach {
            case s: String =>
                val v4 = try {
                    IPv4(s)
                }
                catch {
                    case _: Throwable => None
                }
                val v6 = try {
                    IPv6(s)
                }
                catch {
                    case _: Throwable => None
                }
                if (v4 != None) ipMap += (s -> IPv4(s).addrL)
                else if(v6 != None) ipMap += (s -> IPv6(s).addrBI)
                else throw new Exception("Bad IP address.")
            case v4: IPv4 => ipMap += (v4.ipAddress -> v4.addrL)
            case v6: IPv6 => ipMap += (v6.ipAddress -> v6.addrBI)
            case _ => throw new Exception("Can only accept IP Addresses or Strings.")
        }
    }
    initializeMap()

    def add(ip: Any*): Unit = {
        ip.foreach {
            case s: String =>
                val v4 = try {
                    IPv4(s)
                }
                catch {
                    case _: Throwable => None
                }
                val v6 = try {
                    IPv6(s)
                }
                catch {
                    case _: Throwable => None
                }
                if (v4 != None) ipMap += (s -> IPv4(s).addrL)
                else if(v6 != None) ipMap += (s -> IPv6(s).addrBI)
                else throw new Exception("Bad IP address.")
            case v4: IPv4 => ipMap += (v4.ipAddress -> v4.addrL)
            case v6: IPv6 => ipMap += (v6.ipAddress -> v6.addrBI)
            case _ => throw new Exception("Can only accept IP Addresses or Strings.")
        }
    }
    def remove(ip: Any*): Unit = {
        ip.foreach {
            case s: String => ipMap -= s
            case v4: IPv4 => ipMap -= v4.ipAddress
            case v6: IPv4 => ipMap -= v6.ipAddress
            case _ => throw new Exception("Can only accept IP Addresses or Strings.")
        }
    }
    def contains(ip: Any*): Boolean = {
        ip.foreach {
            case s: String => if (!ipMap.contains(s)) return false
            case v4: IPv4 => if (!ipMap.contains(v4.ipAddress)) return false
            case v6: IPv6 => if (!ipMap.contains(v6.ipAddress)) return false
            case _ => throw new Exception("Can only accept IP Addresses or Strings.")
        }
        true
    }

}