package com.databricks115
import scala.reflect.runtime.universe._


class IPSet (addrs: Seq[IPAddress]) {

    var addrSet: Set[IPAddress] = addrs.toSet

    def contains(addr: IPAddress) = addrSet contains addr
    // def apply
    // def additions
    // def removals
    // def intersect
    // def union
    // def diff
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