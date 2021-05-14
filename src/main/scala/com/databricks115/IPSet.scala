package com.databricks115

case class IPSet(input: Any*) {
    def this() = this(null)
    var ipMap: scala.collection.mutable.Map[String, Either[Long, BigInt]] = scala.collection.mutable.Map()
    var netAVL:AVLTree = AVLTree()
    var root: Node = _

    private def initializeSet(): Unit = {
        input.foreach {
            case s: String =>
                val ip = try {
                    Some(IPAddress(s))
                } catch {
                    case _: Throwable => None
                }
                val net = try {
                    Some(IPNetwork(s))
                } catch {
                    case _: Throwable => None
                }

                if (ip.isDefined) {
                    ip.get.addrNum match {
                        case Left(value) => ipMap += (s -> Left(value))
                        case Right(value) => ipMap += (s -> Right(value))
                    }
                }
                else if (net.isDefined) root = netAVL.insert(root, net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress =>
                ip.addrNum match {
                    case Left(value) => ipMap += (ip.addr -> Left(value))
                    case Right(value) => ipMap += (ip.addr -> Right(value))
                }
            case net: IPNetwork => root = netAVL.insert(root, net)
            case _ => throw new Exception("Bad input.")
        }
    }
    initializeSet()

    def add(ips: Any*): Unit = {
        ips.foreach {
            case s: String =>
                val ip = try {
                    Some(IPAddress(s))
                } catch {
                    case _: Throwable => None
                }
                val net = try {
                    Some(IPNetwork(s))
                } catch {
                    case _: Throwable => None
                }

                if (ip.isDefined) {
                    ip.get.addrNum match {
                        case Left(value) => ipMap += (s -> Left(value))
                        case Right(value) => ipMap += (s -> Right(value))
                    }
                }
                else if(net.isDefined) root = netAVL.insert(root, net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress =>
                ip.addrNum match {
                    case Left(value) => ipMap += (ip.addr -> Left(value))
                    case Right(value) => ipMap += (ip.addr -> Right(value))
                }
            case net: IPNetwork => root = netAVL.insert(root, net)
            case _ => throw new Exception("Bad input.")
        }
    }
    def remove(ips: Any*): Unit = {
        ips.foreach {
            case s: String =>
                val ip = try {
                    Some(IPAddress(s))
                } catch {
                    case _: Throwable => None
                }
                val net = try {
                    Some(IPNetwork(s))
                } catch {
                    case _: Throwable => None
                }
                if (ip.isDefined) ipMap -= s
                else if (net.isDefined) root = netAVL.delete(root, net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress => ipMap -= ip.addr
            case _ => throw new Exception("Bad input.")
        }
    }
    def contains(ips: Any*): Boolean = {
        ips.foreach {
            case s: String => if (!ipMap.contains(s) && !netAVL.contains(root, s)) return false
            case ip: IPAddress => if (!ipMap.contains(ip.addr) && !netAVL.contains(root, ip)) return false
            case net: IPNetwork => if (!netAVL.contains(root, net)) return false
            case _ => throw new Exception("Bad input.")
        }
        true
    }

    def clear(): Unit = {
        ipMap.clear()
        root = null
        netAVL = AVLTree()
    }

}
