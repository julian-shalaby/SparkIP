package com.databricks115
import scala.reflect.runtime.universe._
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet

class IPSet (rangeSet: RangeSet[IPv4]) {
    // rangeSet defaults to a private member
    // we want to access addrSet outside of the class
    var addrSet: RangeSet[IPv4] = rangeSet

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
    
    // Intersection
    def intersect(ipSet: IPSet): IPSet = {
        val notThis: RangeSet[IPv4] = addrSet.complement()
        val notThat: RangeSet[IPv4] = ipSet.addrSet.complement()
        notThis.addAll(notThat)
        new IPSet(notThis.complement())
    }
    def &(ipSet: IPSet): IPSet = this intersect ipSet
    
    // union
    def union(ipSet: IPSet): IPSet = {
        val newRangeSet = TreeRangeSet.create(addrSet)
        newRangeSet.addAll(ipSet.addrSet)
        new IPSet(newRangeSet)
    }
    def |(ipSet: IPSet): IPSet = this union ipSet
    
    // diff
    def diff(ipSet: IPSet): IPSet = {
        val newRangeSet = TreeRangeSet.create(addrSet)
        newRangeSet.removeAll(ipSet.addrSet)
        new IPSet(newRangeSet)
    }
    def &~(ipSet: IPSet): IPSet = this diff ipSet
}

object IPSet {
    def apply[T: TypeTag](seq: Seq[T]): IPSet = {
        typeOf[T] match {
            case ip if ip <:< typeOf[IPNetwork] =>
                val netSeq = seq.asInstanceOf[Seq[IPNetwork]]
                new IPSet(seqToRangeSet(netSeq))
            case str if str =:= typeOf[String] =>
                val strSeq = seq.asInstanceOf[Seq[String]]
                new IPSet(seqToRangeSet(strSeq.map(x => IPNetwork(x))))
            case ipv4 if ipv4 =:= typeOf[IPv4] =>
                val ipSeq = seq.asInstanceOf[Seq[IPv4]]
                new IPSet(seqToRangeSet(ipSeq.map(x => IPNetwork(x))))
        }
    }

    private def seqToRangeSet(seq: Seq[IPNetwork]): RangeSet[IPv4] = {
        var set: TreeRangeSet[IPv4] = TreeRangeSet.create()
        seq.foreach(x => set.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        set
    }

    def apply(ipNet: IPNetwork): IPSet = IPSet(Seq(ipNet))
    def apply(ipStr: String): IPSet = IPSet(Seq(ipStr))
    def apply(ipv4: IPv4): IPSet = IPSet(Seq(ipv4))
    def apply(rangeSet: RangeSet[IPv4]) = new IPSet(rangeSet)
}