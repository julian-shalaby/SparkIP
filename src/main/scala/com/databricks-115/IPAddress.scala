package com.databricks115

import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.DataTypes

class IPAddress (addr: String) extends DataType with Equals {
    
    private val IPv4 = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    
    def isIP: Boolean = {
        addr match {
            case IPv4(o1, o2, o3, o4) => {
                return List(o1, o2, o3, o4).map(_.toInt).filter(x => x < 0 || x > 255).isEmpty
            }
            case _ => false
        }
    }
    
    // Placeholder
    override def asNullable(): DataType = {
        return this;
    }
    
    // Placeholder
    override def defaultSize(): Int = {
        return 1;
    }
    
    override def toString(): String = {
        return addr;
    }
    
    override def canEqual(that: Any): Boolean = that.isInstanceOf[IPAddress]
    
    /**
     * Taken from:
     * https://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
     */
    override def equals(that: Any): Boolean =
    that match {
      case ipAddr: IPAddress =>
        (     (this eq ipAddr)                     //optional, but highly recommended sans very specific knowledge about this exact class implementation
          ||  (     ipAddr.canEqual(this)          //optional only if this class is marked final
                &&  (hashCode == ipAddr.hashCode)  //optional, exceptionally execution efficient if hashCode is cached, at an obvious space inefficiency tradeoff
              )
        )
      case _ =>
        false
    }
    override def hashCode() = addr.hashCode()
}