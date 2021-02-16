package com.databricks115
import scala.reflect.runtime.universe._
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet

class IPSet (addrs: Seq[IPNetwork]) {

    var addrSet: RangeSet[IPv4] = {
        var set: TreeRangeSet[IPv4] = TreeRangeSet.create()
        addrs.foreach(x => set.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        set
    }
    
    // Contains
    def contains(net: IPNetwork): Boolean = addrSet.encloses(Range.closed(net.networkAddress, net.broadcastAddress))
    def contains(addr: IPv4): Boolean = addrSet.contains(addr)
    def contains(addrStr: String): Boolean = this contains IPNetwork(addrStr)

    def apply(net: IPNetwork): Boolean = this contains net
    def apply(addr: IPv4): Boolean = this contains addr
    def apply(addrStr: String): Boolean = this contains addrStr
    
    def isEmpty: Boolean = addrSet.isEmpty

    // Additions
    def addOne(net: IPNetwork) = addrSet.add(Range.closed(net.networkAddress, net.broadcastAddress))
    def addOne(addr: IPv4) = addrSet.add(Range.closed(addr, addr))
    def addOne(addrStr: String): Unit = this addOne IPNetwork(addrStr)
    def +=(net: IPNetwork) = this addOne net
    def +=(addrStr: String) = this addOne addrStr
    def +=(addr: IPv4) = this addOne addr

    def addAll[T: TypeTag](seq: Seq[T]) = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv4]]
            ipSeq.foreach(x => addrSet.add(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this += x)
        case net if net =:= typeOf[IPNetwork] =>
            val netSeq = seq.asInstanceOf[Seq[IPNetwork]]
            netSeq.foreach(x => addrSet.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }

    def ++=[T: TypeTag](ipSeq: Seq[T]) = this addAll ipSeq

    // Removals
    def subtractOne(net: IPNetwork) = addrSet.remove(Range.closed(net.networkAddress, net.broadcastAddress))
    def subtractOne(addr: IPv4) = addrSet.remove(Range.closed(addr, addr))
    def subtractOne(addrStr: String): Unit = this subtractOne IPNetwork(addrStr)
    def -=(net: IPNetwork) = this subtractOne net
    def -=(addrStr: String) = this subtractOne addrStr
    def -=(addr: IPv4) = this subtractOne addr

    def subtractAll[T: TypeTag](seq: Seq[T]) = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv4]]
            ipSeq.foreach(x => addrSet.remove(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this -= x)
        case net if net =:= typeOf[IPNetwork] =>
            val netSeq = seq.asInstanceOf[Seq[IPNetwork]]
            netSeq.foreach(x => addrSet.remove(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }
    def --=[T: TypeTag](ipSeq: Seq[T]) = this subtractAll ipSeq
    
    // // Intersection
    // def intersect(ipSet: IPSet): IPSet = new IPSet((addrSet intersect ipSet.addrSet).toList)
    // def &(ipSet: IPSet): IPSet = this intersect ipSet
    
    // // def union
    // def union(ipSet: IPSet): IPSet = new IPSet((addrSet union ipSet.addrSet).toList)
    // def |(ipSet: IPSet): IPSet = this union ipSet
    
    // // def diff
    // def diff(ipSet: IPSet): IPSet = new IPSet((addrSet diff ipSet.addrSet).toList)
    // def &~(ipSet: IPSet): IPSet = this diff ipSet
}

object IPSet {
    def apply[T: TypeTag](seq: Seq[T]): IPSet = typeOf[T] match {
        case ip if ip <:< typeOf[IPNetwork] =>
            val ipSeq = seq.asInstanceOf[Seq[IPNetwork]]
            new IPSet(ipSeq)
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            new IPSet(strSeq.map(x => IPNetwork(x)))
        case ipv4 if ipv4 =:= typeOf[IPv4] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv4]]
            new IPSet(ipSeq.map(x => IPNetwork(x)))
        //case _ => None
    }

    def apply(ipNet: IPNetwork): IPSet = IPSet(Seq(ipNet))
    def apply(ipStr: String): IPSet = IPSet(Seq(ipStr))
    def apply(ipv4: IPv4): IPSet = IPSet(Seq(ipv4))
}