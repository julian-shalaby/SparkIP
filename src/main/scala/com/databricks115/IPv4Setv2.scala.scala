package com.databricks115
import scala.reflect.runtime.universe._

class IPv4Setv2[T: TypeTag] (someSeq: Seq[T]) {
  var netArr = Array.fill[Set[IPv4]](33)(Set())

  typeOf[T] match {
    case ip if ip =:= typeOf[IPv4Network] =>
      val netSeq = someSeq.asInstanceOf[Seq[IPv4Network]]
      for (net <- netSeq) {
          addNetInternal(net)
      }
    case _ =>
      throw new IllegalArgumentException("IPv4Setv2 takes in a sequence of IPv4Networks as a parameter")
  }

  def addNetInternal(ipNet: IPv4Network): Unit = netArr(ipNet.getCidr) += ipNet.networkAddress

  def contains(ipNet: IPv4Network): Boolean = netArr(ipNet.getCidr)(ipNet.networkAddress)
}

object IPv4Setv2 {
  def apply(ipNet: IPv4Network) = new IPv4Setv2(Seq(ipNet))
  def apply(ipAddr: IPv4) = new IPv4Setv2(Seq(IPv4Network(ipAddr.ipAddress)))
  def apply(ipStr: String) = new IPv4Setv2(Seq(IPv4Network(ipStr)))
}