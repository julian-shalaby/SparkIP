package com.databricks115
import scala.reflect.runtime.universe._
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet

class IPv6Set (rangeSet: RangeSet[IPv6]) {
    // RangeSet defaults to a private member
    // We want to access addrSet outside of the class
    var addrSet: RangeSet[IPv6] = rangeSet

    // Contains
    def contains(net: IPv6Network): Boolean = addrSet.encloses(Range.closed(net.networkAddress, net.broadcastAddress))
    def contains(addr: IPv6): Boolean = addrSet.contains(addr)
    def contains(addrStr: String): Boolean = this contains IPv6Network(addrStr)

    def apply(net: IPv6Network): Boolean = this contains net
    def apply(addr: IPv6): Boolean = this contains addr
    def apply(addrStr: String): Boolean = this contains addrStr
    
    def isEmpty: Boolean = addrSet.isEmpty

    // Additions
    def addOne(net: IPv6Network): Unit = addrSet.add(Range.closed(net.networkAddress, net.broadcastAddress))
    def addOne(addr: IPv6): Unit = addrSet.add(Range.closed(addr, addr))
    def addOne(addrStr: String): Unit = this addOne IPv6Network(addrStr)
    def +=(net: IPv6Network): Unit = this addOne net
    def +=(addrStr: String): Unit = this addOne addrStr
    def +=(addr: IPv6): Unit = this addOne addr

    def addAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv6] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv6]]
            ipSeq.foreach(x => addrSet.add(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this += x)
        case net if net =:= typeOf[IPv6Network] =>
            val netSeq = seq.asInstanceOf[Seq[IPv6Network]]
            netSeq.foreach(x => addrSet.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }

    def ++=[T: TypeTag](ipSeq: Seq[T]): Any = this addAll ipSeq

    // Removals
    def subtractOne(net: IPv6Network): Unit = addrSet.remove(Range.closed(net.networkAddress, net.broadcastAddress))
    def subtractOne(addr: IPv6): Unit = addrSet.remove(Range.closed(addr, addr))
    def subtractOne(addrStr: String): Unit = this subtractOne IPv6Network(addrStr)
    def -=(net: IPv6Network): Unit = this subtractOne net
    def -=(addrStr: String): Unit = this subtractOne addrStr
    def -=(addr: IPv6): Unit = this subtractOne addr

    def subtractAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv6] =>
            val ipSeq = seq.asInstanceOf[Seq[IPv6]]
            ipSeq.foreach(x => addrSet.remove(Range.closed(x, x)))
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this -= x)
        case net if net =:= typeOf[IPv6Network] =>
            val netSeq = seq.asInstanceOf[Seq[IPv6Network]]
            netSeq.foreach(x => addrSet.remove(Range.closed(x.networkAddress, x.broadcastAddress)))
        case _ => Unit
    }
    def --=[T: TypeTag](ipSeq: Seq[T]): Any = this subtractAll ipSeq
    
    // Intersection
    def intersect(ipSet: IPv6Set): IPv6Set = {
        val notThis: RangeSet[IPv6] = addrSet.complement()
        val notThat: RangeSet[IPv6] = ipSet.addrSet.complement()
        notThis.addAll(notThat)
        new IPv6Set(notThis.complement())
    }
    def &(ipSet: IPv6Set): IPv6Set = this intersect ipSet
    
    // Union
    def union(ipSet: IPv6Set): IPv6Set = {
        val newRangeSet = TreeRangeSet.create(addrSet)
        newRangeSet.addAll(ipSet.addrSet)
        new IPv6Set(newRangeSet)
    }
    def |(ipSet: IPv6Set): IPv6Set = this union ipSet
    
    // Diff
    def diff(ipSet: IPv6Set): IPv6Set = {
        val newRangeSet = TreeRangeSet.create(addrSet)
        newRangeSet.removeAll(ipSet.addrSet)
        new IPv6Set(newRangeSet)
    }
    def &~(ipSet: IPv6Set): IPv6Set = this diff ipSet

    override def toString(): String = addrSet.toString()
}

object IPv6Set {
    def apply[T: TypeTag](seq: Seq[T]): IPv6Set = {
        typeOf[T] match {
            case ip if ip =:= typeOf[IPv6Network] =>
                val netSeq = seq.asInstanceOf[Seq[IPv6Network]]
                new IPv6Set(seqToRangeSet(netSeq))
            case str if str =:= typeOf[String] =>
                val strSeq = seq.asInstanceOf[Seq[String]]
                new IPv6Set(seqToRangeSet(strSeq.map(x => IPv6Network(x))))
            case ipv6 if ipv6 =:= typeOf[IPv6] =>
                val ipSeq = seq.asInstanceOf[Seq[IPv6]]
                new IPv6Set(seqToRangeSet(ipSeq.map(x => IPv6Network(x))))
        }
    }

    private def seqToRangeSet(seq: Seq[IPv6Network]): RangeSet[IPv6] = {
        val set: TreeRangeSet[IPv6] = TreeRangeSet.create()
        seq.foreach(x => set.add(Range.closed(x.networkAddress, x.broadcastAddress)))
        set
    }

    def apply(ipNet: IPv6Network): IPv6Set = IPv6Set(Seq(ipNet))
    def apply(ipStr: String): IPv6Set = IPv6Set(Seq(ipStr))
    def apply(ipv6: IPv6): IPv6Set = IPv6Set(Seq(ipv6))
    def apply(rangeSet: RangeSet[IPv6]) = new IPv6Set(rangeSet)
    def apply() = new IPv6Set(TreeRangeSet.create())
}