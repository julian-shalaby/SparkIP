package org.apache.spark
import com.databricks115.{IPAddress, IPNetwork}
import org.apache.spark.sql.types.UDTRegistration

// Then Register the UDT Class!
// NOTE: you have to put this file into the org.apache.spark package!
object package$ extends App {
  UDTRegistration.register(classOf[IPAddress].getName, classOf[IPNetwork].getName)
}
