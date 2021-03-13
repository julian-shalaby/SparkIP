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

@SQLUserDefinedType(udt = classOf[IPv4TypeUDT])
private[sql] class IPv4Type(val IPAddress: String) extends Serializable {
  override def hashCode(): Int = classOf[IPv4TypeUDT].getName.hashCode()
  override def equals(other: Any): Boolean = other match {
    case that: IPv4Type => this.IPAddress == that.IPAddress
    case _ => false
  }
  def IPv4ToLong(ip: String): Long = ip.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
  val addrL = IPv4ToLong(IPAddress)
}

/**
 * User-defined type for [[IPv4]].
 */
private[sql] class IPv4TypeUDT extends UserDefinedType[IPv4Type] {

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
