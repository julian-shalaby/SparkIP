/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.types
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow
import scala.util.matching.Regex

trait IPTraits {
  //conversions
  protected def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
  protected def longToIPv4(ip: Long): IPv4Type = IPv4Type((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
  protected def IPv4subnetToCidr(subnet: String): Int = 32-subnet.split('.').map(Integer.parseInt).reverse.zipWithIndex.
    map{case(value, index)=>value<<index*8}.sum.toBinaryString.count(_ =='0')

  //validations
  protected def IPv4Validation(ip: List[String]): Boolean = if (!ip.map(_.toInt).exists(x => x < 0 || x > 255)) true else false
  //makes sure IPv4 is valid
  def isIP(ip: String): Boolean = {
    ip match {
      case IPv4Address(o1, o2, o3, o4) => IPv4Validation(List(o1, o2, o3, o4))
      case _ => false
    }
  }

  //regex
  protected val IPv4Address: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
}

@SQLUserDefinedType(udt = classOf[IPv4TypeUDT])
private[sql] case class IPv4Type(val IPAddress: String) extends Serializable with IPTraits {
  //udt stuff
  override def hashCode(): Int = classOf[IPv4TypeUDT].getName.hashCode()
  override def equals(other: Any): Boolean = other match {
    case that: IPv4Type => this.IPAddress == that.IPAddress
    case _ => false
  }

  //our stuff goes here
  //IPv4 as a number
  val addrL: Long = IPv4ToLong(IPAddress)
  require(isIP(IPAddress), "IPv4 invalid.")

  //compare operations
  def <(that: IPv4Type): Boolean = this.addrL < that.addrL
  def >(that: IPv4Type): Boolean = this.addrL > that.addrL
  def <=(that: IPv4Type): Boolean = this.addrL <= that.addrL
  def >=(that: IPv4Type): Boolean = this.addrL >= that.addrL

  //Return network address of IP address
  def mask(maskIP: Int): IPv4Type = {
    require(maskIP >= 0 && maskIP <= 32, "Can only mask 0-32.")
    longToIPv4(0xFFFFFFFF << (32 - maskIP) & addrL)
  }
  def mask(maskIP: String): IPv4Type = {
    require(isIP(maskIP), "IPv4 invalid.")
    longToIPv4(IPv4ToLong(maskIP) & addrL)
  }

  // Address Types
  val isMulticast: Boolean = if (addrL >= 3758096384L && addrL <= 4026531839L) true else false
  val isPrivate: Boolean = if (
    (addrL >= 167772160L && addrL <= 184549375L) ||
      (addrL >= 2886729728L && addrL <= 2887778303L) ||
      (addrL >= 3232235520L && addrL <= 3232301055L)
  ) true else false
  val isGlobal: Boolean = !isPrivate
  val isUnspecified: Boolean = if (addrL == 0) true else false
  val isLoopback: Boolean = if (addrL >= 2130706432L && addrL <= 2147483647L) true else false
  val isLinkLocal: Boolean = if (addrL >= 2851995648L && addrL <= 2852061183L) true else false
  val isReserved: Boolean = if (
    (addrL >= 0L && addrL <= 16777215L) ||
      isPrivate ||
      (addrL >= 1681915904L && addrL <= 1686110207L) ||
      isLoopback ||
      isLinkLocal ||
      (addrL >= 3221225472L && addrL <= 3221225727L) ||
      (addrL >= 3221225984L && addrL <= 3221226239L) ||
      (addrL >= 3227017984L && addrL <= 3227018239L) ||
      (addrL >= 3323068416L && addrL <= 3323199487L) ||
      (addrL >= 3325256704L && addrL <= 3325256959L) ||
      (addrL >= 3405803776L && addrL <= 3405804031L) ||
      isMulticast ||
      (addrL >= 4026531840L && addrL <= 4294967294L) ||
      (addrL == 4294967295L)
  ) true else false
}

/**
 * User-defined type for [[IPv4]].
 */
private[sql] class IPv4TypeUDT extends UserDefinedType[IPv4Type] {
  //udt stuff
  override def sqlType: StructType = {
    StructType(Seq(
      StructField("IPAddress", StringType, nullable = false)
    ))
  }
  override def serialize(obj: IPv4Type): InternalRow = {
    val row = new GenericInternalRow(1)
    row.setString(0, obj.IPAddress)
    row
  }
  override def deserialize(datum: Any): IPv4Type = {
    datum match {
      case row: InternalRow =>
        new IPv4Type(row.getString(0))
    }
  }
  override def typeName: String = "ip"
  override def userClass: Class[IPv4Type] = classOf[IPv4Type]
  private[spark] override def asNullable: IPv4TypeUDT = this
}

/*

import org.apache.spark.sql.types.IPv4Type
import org.apache.spark.sql.{DataFrame, Dataset}

val ip = Seq(
  new IPv4Type("0.0.0.0"),
  new IPv4Type("192.0.0.0"),
  new IPv4Type("239.0.0.0")
).toDS

ip.filter(i => i.addrL > 0).show()

val path = "/Users/julianshalaby/IdeaProjects/Databricks-115/IPText.json"
val IPv4DS: Dataset[IPv4Type] = spark.read.json(path).as[IPv4Type]

 */