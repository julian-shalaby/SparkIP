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
import scala.util.matching.Regex

trait IPv4Traits {
  //conversions
  protected def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
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
  //1.1.1.1/16 format
  protected val NetworkCIDR: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/(\d{1,2})""".r
  //1.1.1.1/255.255.0.0 format
  protected val NetworkDottedDecimal: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
  //Address 1.1.1.1 Netmask 255.255.255.0 format
  protected val NetworkVerboseDottedDecimal: Regex = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
  //1.1.1.1-2.2.2.2 format
  protected val NetworkIPRange: Regex = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\-([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r

}

trait sharedIPTraits {
  protected def longToIPv4(ip: Long): IPv4 = IPv4((for(a<-3 to 0 by -1) yield ((ip>>(a*8))&0xff).toString).mkString("."))
}

@SQLUserDefinedType(udt = classOf[IPv4TypeUDT])
private[sql] class IPv4Type(addr: String) extends Serializable with IPv4Traits with sharedIPTraits {

  val addrL: Long = IPv4ToLong(addr)
  require(isIP(addr), "IPv4 invalid.")

  //compare operations
  def <(that: IPv4): Boolean = this.addrL < that.addrL
  def >(that: IPv4): Boolean = this.addrL > that.addrL
  def <=(that: IPv4): Boolean = this.addrL <= that.addrL
  def >=(that: IPv4): Boolean = this.addrL >= that.addrL

  //Return network address of IP address
  def mask(maskIP: Int): IPv4 = {
    require(maskIP >= 0 && maskIP <= 32, "Can only mask 0-32.")
    longToIPv4(0xFFFFFFFF << (32 - maskIP) & addrL)
  }
  def mask(maskIP: String): IPv4 = {
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

private[sql] class IPv4TypeUDT extends UserDefinedType[IPv4Type] {

  //for SQL datatype stuff. idk what to do yet
  override def sqlType: DataType = ArrayType(this, false)
  override def serialize(p: IPv4Type): Any = ???
  override def deserialize(datum: Any): IPv4Type = {
    datum match {
      case _ => new IPv4Type("0.0.0.0")
    }
  }

  //needed for udt i think
  override def userClass: Class[IPv4Type] = classOf[IPv4Type]
  private[spark] override def asNullable: IPv4TypeUDT = this
}

/*
./bin/spark-shell --jars /Users/julianshalaby/IdeaProjects/Databricks-115/spark/sql/catalyst/target/spark-catalyst_2.12-3.2.0-SNAPSHOT.jar

import org.apache.spark.sql.types._

val ip: IPv4Type = new IPv4Type("192.0.0.0")

val ip2 = Seq(
  new IPv4Type("192.0.0.0"),
  new IPv4Type("0.0.0.0"),
  new IPv4Type("78.0.6.0")
).toDF

ip2.select($"addr").show()

ip2.filter(ip => ip.isMulticast).show()

 */