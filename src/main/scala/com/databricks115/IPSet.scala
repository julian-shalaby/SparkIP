package com.databricks115
import scala.reflect.runtime.universe._
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet

class IPSet {
    // RangeSet defaults to a private member
    // We want to access addrSet outside of the class
    var ipv4Set: IPv4Set = IPv4Set()
    var ipv6Set: IPv6Set = IPv6Set()

    // Contains
    def contains(net: IPv4Network): Boolean = ipv4Set(net)
    def contains(addr: IPv4): Boolean = ipv4Set(addr)
    def contains(net: IPv6Network): Boolean = ipv6Set(net)
    def contains(addr: IPv6): Boolean = ipv6Set(addr)

    def contains(addrStr: String): Boolean = {
        var res = false
        if(addrStr contains ":") res = ipv6Set contains IPv6Network(addrStr)
        else if (addrStr contains ".") res = ipv4Set contains IPv4Network(addrStr)
        res
    }

    def apply(net: IPv4Network): Boolean = this contains net
    def apply(addr: IPv4): Boolean = this contains addr
    def apply(net: IPv6Network): Boolean = this contains net
    def apply(addr: IPv6): Boolean = this contains addr
    def apply(addrStr: String): Boolean = this contains addrStr
    
    def isEmpty: Boolean = (ipv4Set isEmpty) && (ipv6Set isEmpty)

    // Additions
    def addOne(net: IPv4Network): Unit = ipv4Set += net
    def addOne(addr: IPv4): Unit = ipv4Set += addr
    def addOne(net: IPv6Network): Unit = ipv6Set += net
    def addOne(addr: IPv6): Unit = ipv6Set += addr
    def addOne(addrStr: String): Unit = {
        if(addrStr contains ":") ipv6Set += IPv6Network(addrStr)
        else if (addrStr contains ".") ipv4Set += IPv4Network(addrStr)
    }

    def +=(net: IPv4Network): Unit = this addOne net
    def +=(addr: IPv4): Unit = this addOne addr
    def +=(net: IPv6Network): Unit = this addOne net
    def +=(addr: IPv6): Unit = this addOne addr
    def +=(addrStr: String): Unit = this addOne addrStr
    
    def addAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            ipv4Set ++= seq
        case ip if ip =:= typeOf[IPv6] =>
            ipv6Set ++= seq
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this += x)
        case net if net =:= typeOf[IPv4Network] =>
            ipv4Set ++= seq
        case net if net =:= typeOf[IPv6Network] =>
            ipv6Set ++= seq
        case _ => Unit
    }

    def ++=[T: TypeTag](ipSeq: Seq[T]): Any = this addAll ipSeq

    // Removals
    def subtractOne(net: IPv4Network): Unit = ipv4Set -= net
    def subtractOne(addr: IPv4): Unit = ipv4Set -= addr
    def subtractOne(net: IPv6Network): Unit = ipv6Set -= net
    def subtractOne(addr: IPv6): Unit = ipv6Set -= addr
    def subtractOne(addrStr: String): Unit = {
        if(addrStr contains ":") ipv6Set -= IPv6Network(addrStr)
        else if (addrStr contains ".") ipv4Set -= IPv4Network(addrStr)
    }
    
    def -=(net: IPv4Network): Unit = this subtractOne net
    def -=(addr: IPv4): Unit = this subtractOne addr
    def -=(net: IPv6Network): Unit = this subtractOne net
    def -=(addr: IPv6): Unit = this subtractOne addr
    def -=(addrStr: String): Unit = this subtractOne addrStr

    def subtractAll[T: TypeTag](seq: Seq[T]): Any = typeOf[T] match {
        case ip if ip =:= typeOf[IPv4] =>
            ipv4Set --= seq
        case ip if ip =:= typeOf[IPv6] =>
            ipv6Set --= seq
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            strSeq.foreach(x => this -= x)
        case net if net =:= typeOf[IPv4Network] =>
            ipv4Set --= seq
        case net if net =:= typeOf[IPv6Network] =>
            ipv6Set --= seq
        case _ => Unit
    }
    def --=[T: TypeTag](ipSeq: Seq[T]): Any = this subtractAll ipSeq
    
    // Intersection
    def intersect(that: IPSet): IPSet = {
        var newSet = IPSet()
        newSet.ipv4Set = this.ipv4Set & that.ipv4Set
        newSet.ipv6Set = this.ipv6Set & that.ipv6Set
        print(newSet.ipv4Set)
        newSet
    }
    def &(that: IPSet): IPSet = this intersect that
    
    // Union
    def union(that: IPSet): IPSet = {
        var newSet = IPSet()
        newSet.ipv4Set = this.ipv4Set | that.ipv4Set
        newSet.ipv6Set = this.ipv6Set | that.ipv6Set
        newSet
    }
    def |(that: IPSet): IPSet = this union that
    
    // Diff
    def diff(that: IPSet): IPSet = {
        var newSet = IPSet()
        newSet.ipv4Set = this.ipv4Set &~ that.ipv4Set
        newSet.ipv6Set = this.ipv6Set &~ that.ipv6Set
        newSet
    }
    def &~(that: IPSet): IPSet = this diff that
}

object IPSet {
    def apply() = new IPSet()
}