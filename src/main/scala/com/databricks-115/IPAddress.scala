package com.databricks115
import org.apache.spark.sql.types.DataType

class IPAddress (addr: String) extends DataType with Equals {
    
    private val IPv4 = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r

    val addrL: Long = addr.split("\\.").reverse.zipWithIndex.map(a => a._1.toInt * math.pow(256, a._2).toLong).sum
    require(addrL >= 0L, "The address must be >= 0.0.0.0.")
    require(addrL <= 4294967295L, "The address must be <= 255.255.255.255.")
    
    def isIP: Boolean = {
        addr match {
            case IPv4(o1, o2, o3, o4) => {
                return List(o1, o2, o3, o4).map(_.toInt).filter(x => x < 0 || x > 255).isEmpty
            }
            case _ => false
        }
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
    
    // Placeholder
    override def asNullable(): DataType = return this;
    
    // Placeholder
    override def defaultSize(): Int = return 1;
    
    override def toString(): String = return addr
    
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