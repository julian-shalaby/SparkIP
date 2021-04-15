package com.databricks115
import scala.reflect.runtime.universe._

class IPv4Setv2 (someSeq: Seq[IPv4Network]) {
  var netArr = Array.fill[Set[IPv4]](33)(Set())

  validateSomeSeq(someSeq)

  def validateSomeSeq[T: TypeTag](someSeq: Seq[T]) = {
    typeOf[T] match {
      case ip if ip =:= typeOf[IPv4Network] =>
        val netSeq = someSeq.asInstanceOf[Seq[IPv4Network]]
        for (net <- netSeq) {
            addNetInternal(net)
        }
      case _ =>
        throw new IllegalArgumentException("IPv4Setv2 takes in a sequence of IPv4Networks as a parameter")
    }
  }

  def addNetInternal(ipNet: IPv4Network): Unit = netArr(ipNet.getCidr) += ipNet.networkAddress

  def contains(ipNet: IPv4Network): Boolean = netArr(ipNet.getCidr)(ipNet.networkAddress)
  def contains(ipStr: String): Boolean = contains(IPv4Network(ipStr))
  def contains(ipv4: IPv4): Boolean = netArr.foldLeft(false)((f, set) => f || set(ipv4))
}

object IPv4Setv2 {
  def apply(ipNet: IPv4Network) = new IPv4Setv2(Seq(ipNet))
  def apply(ipAddr: IPv4) = new IPv4Setv2(Seq(IPv4Network(ipAddr.ipAddress)))
  def apply(ipStr: String) = new IPv4Setv2(Seq(IPv4Network(ipStr)))

  def apply[T: TypeTag](seq: Seq[T]): IPv4Setv2 = {
        typeOf[T] match {
            case ip if ip <:< typeOf[IPv4Network] =>
                val netSeq = seq.asInstanceOf[Seq[IPv4Network]]
                new IPv4Setv2(netSeq)
            case str if str =:= typeOf[String] =>
                val strSeq = seq.asInstanceOf[Seq[String]]
                new IPv4Setv2(strSeq.map(x => IPv4Network(x)))
            case ipv4 if ipv4 =:= typeOf[IPv4] =>
                val ipSeq = seq.asInstanceOf[Seq[IPv4]]
                new IPv4Setv2(ipSeq.map(x => IPv4Network(x)))
        }
    }
}