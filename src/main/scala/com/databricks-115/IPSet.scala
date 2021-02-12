package com.databricks115
import scala.reflect.runtime.universe._


class IPSet (addrs: Seq[IPAddress]) {

    var addrSet: Set[IPAddress] = addrs.toSet

    // Contains
    def contains(addr: IPAddress): Boolean = addrSet contains addr
    def contains(addrStr: String): Boolean = addrSet contains IPv4(addrStr)

    def apply(addr: IPAddress): Boolean = this contains addr
    def apply(addrStr: String): Boolean = this contains addrStr
    def isEmpty: Boolean = addrSet.isEmpty    

    // Additions
    def addOne(addr: IPAddress) = addrSet += addr
    def addOne(addrStr: String) = addrSet += IPv4(addrStr)
    def +=(addr: IPAddress) = this addOne addr

    def addAll[T: TypeTag](seq: Seq[T]) = typeOf[T] match {
        case ip if ip <:< typeOf[IPAddress] =>
            val ipSeq = seq.asInstanceOf[Seq[IPAddress]]
            addrSet ++= ipSeq
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            addrSet ++= strSeq.map(x => IPv4(x))
        case _ => Unit
    }

    def ++=(ipSeq: Seq[IPAddress]) = this addAll ipSeq
    
    // Removals
    def subtractOne(addr: IPAddress) = addrSet -= addr
    def subtractOne(addrStr: String) = addrSet -= IPv4(addrStr)
    def -=(addr: IPAddress) = this subtractOne addr

    def subtractAll[T: TypeTag](seq: Seq[T]) = typeOf[T] match {
        case ip if ip <:< typeOf[IPAddress] =>
            val ipSeq = seq.asInstanceOf[Seq[IPAddress]]
            addrSet --= ipSeq
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            addrSet --= strSeq.map(x => IPv4(x))
        case _ => Unit
    }
    def --=(ipSeq: Seq[IPAddress]) = this subtractAll ipSeq
    
    // Intersection
    def intersect(ipSet: IPSet): IPSet = new IPSet((addrSet intersect ipSet.addrSet).toList)
    def &(ipSet: IPSet): IPSet = this intersect ipSet
    
    // def union
    def union(ipSet: IPSet): IPSet = new IPSet((addrSet union ipSet.addrSet).toList)
    def |(ipSet: IPSet): IPSet = this union ipSet
    
    // def diff
    def diff(ipSet: IPSet): IPSet = new IPSet((addrSet diff ipSet.addrSet).toList)
    def &~(ipSet: IPSet): IPSet = this diff ipSet
}

object IPSet {
    def apply[T: TypeTag](seq: Seq[T]): IPSet = typeOf[T] match {
        case ip if ip <:< typeOf[IPAddress] =>
            val ipSeq = seq.asInstanceOf[Seq[IPAddress]]
            new IPSet(ipSeq)
        case str if str =:= typeOf[String] =>
            val strSeq = seq.asInstanceOf[Seq[String]]
            new IPSet(strSeq.map(x => IPv4(x)))
        //case _ => None
    }
}