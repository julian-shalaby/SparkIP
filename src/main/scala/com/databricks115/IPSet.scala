package com.databricks115

case class IPSet(input: Any*) {
    def this() = this(null)

    private val ipMap: scala.collection.mutable.Map[String, Either[Long, BigInt]] = scala.collection.mutable.Map()
    private var netAVL:AVLTree = AVLTree()
    def ==(that: IPSet): Boolean = this.returnAll().equals(that.returnAll())
    def !=(that: IPSet): Boolean = !this.returnAll().equals(that.returnAll())
    def length: Int = ipMap.size + netAVL.length

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
                else if (net.isDefined) netAVL.insert(net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress =>
                ip.addrNum match {
                    case Left(value) => ipMap += (ip.addr -> Left(value))
                    case Right(value) => ipMap += (ip.addr -> Right(value))
                }
            case net: IPNetwork => netAVL.insert(net)
            case set: Set[Any] => set.foreach(i => add(i))
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
                else if(net.isDefined) netAVL.insert(net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress =>
                ip.addrNum match {
                    case Left(value) => ipMap += (ip.addr -> Left(value))
                    case Right(value) => ipMap += (ip.addr -> Right(value))
                }
            case net: IPNetwork => netAVL.insert(net)
            case set: Set[Any] => set.foreach(i => add(i))
            case _ => throw new Exception("Bad input.")
        }
        SparkIP.update_sets()
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
                else if (net.isDefined) netAVL.delete(net.get)
                else throw new Exception("Bad input.")
            case ip: IPAddress => ipMap -= ip.addr
            case net: IPNetwork => netAVL.delete(net)
            case set: Set[Any] => set.foreach(i => remove(i))
            case _ => throw new Exception("Bad input.")
        }
        SparkIP.update_sets()
    }

    def contains(ip: Any): Boolean = {
        ip match {
            case s: String => if (ipMap.contains(s) || netAVL.contains(s)) return true
            case ip: IPAddress => if (ipMap.contains(ip.addr) || netAVL.contains(ip)) return true
            case net: IPNetwork => if (netAVL.contains(net)) return true
            case _ => throw new Exception("Bad input.")
        }
        false
    }

    def clear(): Unit = {
        ipMap.clear()
        netAVL = AVLTree()
    }

    def showAll(): Unit = {
        println("IP addresses:")
        ipMap.keys.foreach(println)
        println("IP networks:")
        netAVL.preOrder()
    }

    def returnAll(): Set[Any] = {
        var setList = Set[Any]()
        ipMap.keys.foreach(ip => setList += IPAddress(ip))
        netAVL.returnAll().foreach(net => setList += net)
        Set(setList)
    }

    def isEmpty: Boolean = ipMap.isEmpty && netAVL.length == 0

    def intersection(set2: IPSet): IPSet = {
        val intersectSet = IPSet()
        ipMap.keys.foreach(ip => if (set2.contains(ip)) intersectSet.add(ip))
        netAVL.netIntersect(set2).foreach(net => intersectSet.add(net))
        intersectSet
    }

    def union(set2: IPSet): IPSet = {
        val unionSet = IPSet()
        ipMap.keys.foreach(unionSet.add(_))
        set2.ipMap.keys.foreach(unionSet.add(_))
        netAVL.returnAll().foreach(unionSet.add(_))
        set2.netAVL.returnAll().foreach(unionSet.add(_))
        unionSet
    }

    def diff(set2: IPSet): IPSet = {
        val diffSet = IPSet()
        ipMap.keys.foreach(ip => if (!set2.contains(ip)) diffSet.add(ip))
        netAVL.returnAll().foreach(net => if (!set2.contains(net)) diffSet.add(net))
        diffSet
    }
}
