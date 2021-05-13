package com.databricks115

case class IPSet(ipAddresses: Any*) {
    def this() = this(null)
    var ipMap: Map[String, Either[Long, BigInt]] = Map()
    var netAVL:AVLTree = AVLTree()
    var root: Node = _

    private def initializeSet(): Unit = {
        ipAddresses.foreach {
            case s: String =>
                val v4 = try {
                    Some(IPv4(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6 = try {
                    Some(IPv6(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v4Net = try {
                    Some(IPv4Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6Net = try {
                    Some(IPv6Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                if (v4.isDefined) ipMap += (s -> Left(v4.get.addrL))
                else if(v6.isDefined) ipMap += (s -> Right(v6.get.addrBI))
                else if(v4Net.isDefined) root = netAVL.insert(root, v4Net.get)
                else if(v6Net.isDefined) root = netAVL.insert(root, v6Net.get)
                else throw new Exception("Bad input.")
            case v4: IPv4 => ipMap += (v4.ipAddress -> Left(v4.addrL))
            case v6: IPv6 => ipMap += (v6.ipAddress -> Right(v6.addrBI))
            case v4Net: IPv4Network => root = netAVL.insert(root, v4Net)
            case v6Net: IPv6Network => root = netAVL.insert(root, v6Net)
            case _ => throw new Exception("Bad input.")
        }
    }
    initializeSet()

    def add(ip: Any*): Unit = {
        ip.foreach {
            case s: String =>
                val v4 = try {
                    Some(IPv4(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6 = try {
                    Some(IPv6(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v4Net = try {
                    Some(IPv4Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6Net = try {
                    Some(IPv6Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                if (v4.isDefined) ipMap += (s -> Left(v4.get.addrL))
                else if(v6.isDefined) ipMap += (s -> Right(v6.get.addrBI))
                else if(v4Net.isDefined) root = netAVL.insert(root, v4Net.get)
                else if(v6Net.isDefined) root = netAVL.insert(root, v6Net.get)
                else throw new Exception("Bad input.")
            case v4: IPv4 => ipMap += (v4.ipAddress -> Left(v4.addrL))
            case v6: IPv6 => ipMap += (v6.ipAddress -> Right(v6.addrBI))
            case v4Net: IPv4Network => root = netAVL.insert(root, v4Net)
            case v6Net: IPv6Network => root = netAVL.insert(root, v6Net)
            case _ => throw new Exception("Bad input.")
        }
    }
    def remove(ip: Any*): Unit = {
        ip.foreach {
            case s: String =>
                val v4 = try {
                    Some(IPv4(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6 = try {
                    Some(IPv6(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v4Net = try {
                    Some(IPv4Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                val v6Net = try {
                    Some(IPv6Network(s))
                }
                catch {
                    case _: Throwable => None
                }
                if (v4.isDefined || v6.isDefined) ipMap -= s
                else if (v4Net.isDefined) root = netAVL.delete(root, v4Net.get)
                else if (v6Net.isDefined) root = netAVL.delete(root, v6Net.get)
                else throw new Exception("Bad input.")
            case v4: IPv4 => ipMap -= v4.ipAddress
            case v6: IPv4 => ipMap -= v6.ipAddress
            case _ => throw new Exception("Bad input.")
        }
    }
    def contains(ip: Any*): Boolean = {
        ip.foreach {
            case s: String => if (!ipMap.contains(s) && !netAVL.AVLSearch(root, s) && !netAVL.AVLSearchIP(root, s))
                return false
            case v4: IPv4 => if (!ipMap.contains(v4.ipAddress) && !netAVL.AVLSearchIP(root, v4))
                return false
            case v6: IPv6 => if (!ipMap.contains(v6.ipAddress) && !netAVL.AVLSearchIP(root, v6)) return false
            case v4Net: IPv4Network => if (!netAVL.AVLSearch(root, v4Net)) return false
            case v6Net: IPv4Network => if (!netAVL.AVLSearch(root, v6Net)) return false
            case _ => throw new Exception("Bad input.")
        }
        true
    }

}