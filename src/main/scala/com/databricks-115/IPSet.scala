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
    def +=(addr: IPAddress) = this addOne addr

    def addAll(ipSeq: Seq[IPAddress]) = addrSet ++= ipSeq
    def ++=(ipSeq: Seq[IPAddress]) = this addAll ipSeq
    
    // Removals
    def subtractOne(addr: IPAddress) = addrSet -= addr
    def -=(addr: IPAddress) = this subtractOne addr

    def subtractAll(ipSeq: Seq[IPAddress]) = addrSet --= ipSeq
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

/*
    TODO:
    Create alternative constructors
 */
object IPSet {
    // def Apply[T : TypeTag](addrs: Seq[T]) = {
    //     addrs match {
    //         case strSeq: Seq[String @unchecked] if typeOf[T] =:= typeOf[String] => new IPSet(addrs.map(x => x match {
    //             case str: String => IPv4(str)
    //             case _ => IPv4("0.0.0.0")
    //         }))
    //         case ipSeq: Seq[IPAddress @unchecked] if typeOf[T] =:= typeOf[IPAddress] => new IPSet(ipSeq)
    //         case _ => None
    //     }
    // }
    // def Apply[T: TypeTag](addrs: Seq[T]) = new IPSet(addrs.map(x => x match {
    //     case str: String => new IPv4(str)
    //     case ip: IPAddress => ip
    // }))
}