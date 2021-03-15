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
    def contains(net: IPv4Network): Boolean = addrSet.encloses(Range.closed(net.networkAddress, net.broadcastAddress))
    def contains(addr: IPv4): Boolean = addrSet.contains(addr)
    def contains(addrStr: String): Boolean = this contains IPv4Network(addrStr)

    def apply(net: IPv4Network): Boolean = this contains net
    def apply(addr: IPv4): Boolean = this contains addr
    def apply(addrStr: String): Boolean = this contains addrStr
    
    def isEmpty: Boolean = addrSet.isEmpty

    // Additions
    def addOne(net: IPv4Network): Unit = addrSet.add(Range.closed(net.networkAddress, net.broadcastAddress))
    def addOne(addr: IPv4): Unit = addrSet.add(Range.closed(addr, addr))
    def addOne(addrStr: String): Unit = this addOne IPv4Network(addrStr)
    def +=(net: IPv4Network): Unit = this addOne net
    def +=(addrStr: String): Unit = this addOne addrStr
    def +=(addr: IPv4): Unit = this addOne addr

    def addAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv4]]
            ipSeq.foreach(x => addrSet.add(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this += x)
        case net if net =:= typeOf[IPv4Network] =>
            val netSeq = seq.asInstanceOf[Seq[IPv4Network]]
            netSeq.foreach(x => addrSet.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }

    def ++=[T: TypeTag](ipSeq: Seq[T]): Any = this addAll ipSeq

    // Removals
    def subtractOne(net: IPv4Network): Unit = addrSet.remove(Range.closed(net.networkAddress, net.broadcastAddress))
    def subtractOne(addr: IPv4): Unit = addrSet.remove(Range.closed(addr, addr))
    def subtractOne(addrStr: String): Unit = this subtractOne IPv4Network(addrStr)
    def -=(net: IPv4Network): Unit = this subtractOne net
    def -=(addrStr: String): Unit = this subtractOne addrStr
    def -=(addr: IPv4): Unit = this subtractOne addr

    def subtractAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv4]]
            ipSeq.foreach(x => addrSet.remove(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this -= x)
        case net if net =:= typeOf[IPv4Network] =>
            val netSeq = seq.asInstanceOf[Seq[IPv4Network]]
            netSeq.foreach(x => addrSet.remove(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }
    def --=[T: TypeTag](ipSeq: Seq[T]): Any = this subtractAll ipSeq
    
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
            case ip if ip <:< typeOf[IPv4Network] =>
                val netSeq = seq.asInstanceOf[Seq[IPv4Network]]
                new IPSet(seqToRangeSet(netSeq))
            case str if str =:= typeOf[String] =>
                val strSeq = seq.asInstanceOf[Seq[String]]
                new IPSet(seqToRangeSet(strSeq.map(x => IPv4Network(x))))
            case ipv4 if ipv4 =:= typeOf[IPv4] =>
                val ipSeq = seq.asInstanceOf[Seq[IPv4]]
                new IPSet(seqToRangeSet(ipSeq.map(x => IPv4Network(x))))
        }
    }

    private def seqToRangeSet(seq: Seq[IPv4Network]): RangeSet[IPv4] = {
        val set: TreeRangeSet[IPv4] = TreeRangeSet.create()
        seq.foreach(x => set.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        set
    }

    def apply(ipNet: IPv4Network): IPSet = IPSet(Seq(ipNet))
    def apply(ipStr: String): IPSet = IPSet(Seq(ipStr))
    def apply(ipv4: IPv4): IPSet = IPSet(Seq(ipv4))
    def apply(rangeSet: RangeSet[IPv4]) = new IPSet(rangeSet)
}