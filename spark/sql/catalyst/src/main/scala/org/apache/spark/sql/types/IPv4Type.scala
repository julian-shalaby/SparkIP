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

@SQLUserDefinedType(udt = classOf[IPUDT])
private[sql] trait IPv4 extends Serializable {
  def addr: String
  def addrL: Long
}

private[sql] class IPUDT extends UserDefinedType[IPv4] {

  //for SQL datatype stuff. idk what to do yet
  override def sqlType: StructType = {
    StructType(Seq(
      StructField("addr", StringType, nullable = false)
    ))
  }
  override def serialize(obj: IPv4): InternalRow = {
    val row = new GenericInternalRow(1)
    obj match {
      case _ =>
        row.setString(0, "0.0.0.0")
    }
    row
  }
  override def deserialize(datum: Any): IPv4 = {
    datum match {
      case _ => new IPv4Type("0.0.0.0")
    }
  }

  //needed for udt i think
  override def userClass: Class[IPv4] = classOf[IPv4]
  private[spark] override def asNullable: IPUDT = this

  override def equals(o: Any): Boolean = {
    o match {
      case _ => true
    }
  }

  override def hashCode(): Int = classOf[IPUDT].getName.hashCode()

  override def typeName: String = "ip"

}

@SQLUserDefinedType(udt = classOf[IPUDT])
class IPv4Type (val addr: String) extends IPv4 {
  def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
  override def addrL: Long = IPv4ToLong(addr)
}

object IPv4Type {
  def apply(addr: String): IPv4Type = {
    new IPv4Type(addr)
  }
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
 */

/*
import org.apache.spark.sql.{DataFrame, SparkSession}

  val path = "/Users/julianshalaby/IdeaProjects/Databricks-115/IPText.json"

  val IPv4DF: DataFrame = spark.read.json(path)

  IPv4DF.createOrReplaceTempView("IPv4")

 */

/*

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
 */