//package com.SparkIP
//
//import org.apache.spark.sql.types.{DataType, SQLUserDefinedType, StringType, UserDefinedType}
//
//
//@SQLUserDefinedType(udt = classOf[IPAddressUDT])
//case class IPAddressSQL(ip: String) extends Serializable {
//  val ipAddr: IPAddress = IPAddress(ip)
//}
//
///**
// * User-defined type for [[IPAddressSQL]].
// */
//class IPAddressUDT extends UserDefinedType[IPAddressSQL] {
//  override def sqlType: DataType = StringType
//
//  // somehow link ours???
////  override def pyUDT: String = "pyspark.testing.sqlutils.ExamplePointUDT"
//
//  override def serialize(obj: IPAddressSQL): String = {
//    obj.ipAddr.addr
//  }
//  override def deserialize(datum: Any): IPAddressSQL = {
//    datum match {
//      case s: String => IPAddressSQL(s)
//    }
//  }
//}
//
///**
//* template for when Scala UDTs are public
// */