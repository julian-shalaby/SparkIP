package com.databricks115

case class IPAddress(addr: String) extends IPTraits with Ordered[IPAddress] {
  // IP as a number
  val addrNum: Either[Long, BigInt] = IPToNum(addr)

  // Compare operations
  override def <(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 < value2
      case (Right(value1), Right(value2)) => value1 < value2
      case (Left(value1), Right(value2)) => value1 < value2
      case (Right(value1), Left(value2)) => value1 < value2
    }
  }
  override def >(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 > value2
      case (Right(value1), Right(value2)) => value1 > value2
      case (Left(value1), Right(value2)) => value1 > value2
      case (Right(value1), Left(value2)) => value1 > value2
    }
  }
  override def <=(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 <= value2
      case (Right(value1), Right(value2)) => value1 <= value2
      case (Left(value1), Right(value2)) => value1 <= value2
      case (Right(value1), Left(value2)) => value1 <= value2
    }
  }
  override def >=(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 >= value2
      case (Right(value1), Right(value2)) => value1 >= value2
      case (Left(value1), Right(value2)) => value1 >= value2
      case (Right(value1), Left(value2)) => value1 >= value2
    }
  }
  def ==(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 == value2
      case (Right(value1), Right(value2)) => value1 == value2
      case (Left(value1), Right(value2)) => value1 == value2
      case (Right(value1), Left(value2)) => value1 == value2
    }
  }
  def !=(that: IPAddress): Boolean = {
    (this.addrNum, that.addrNum) match {
      case (Left(value1), Left(value2)) => value1 != value2
      case (Right(value1), Right(value2)) => value1 != value2
      case (Left(value1), Right(value2)) => value1 != value2
      case (Right(value1), Left(value2)) => value1 != value2
    }
  }
  def compare(that: IPAddress): Int = {
    if (this == that) 0
    else if (this < that) -1
    else 1
  }

  // Address types
  lazy val isPrivate: Boolean = {
    addrNum match {
      case Left(value) =>
        value >= 167772160L && value <= 184549375L ||
        value >= 2886729728L && value <= 2887778303L ||
        value >= 3232235520L && value <= 3232301055L
      case Right(_) => false
    }
  }
  lazy val isGlobal: Boolean = !isPrivate
  lazy val isLinkLocal: Boolean = {
    addrNum match {
      case Left(value) => value >= 2851995648L && value <= 2852061183L
      case Right(value) =>
        value >= BigInt("338288524927261089654018896841347694592") &&
        value<= BigInt("338620831926207318622244848606417780735")
    }
  }
  lazy val isLoopback: Boolean = {
    addrNum match {
      case Left(value) => value >= 2130706432L && value <= 2147483647L
      case Right(value) => value == 1
    }
  }
  lazy val isMulticast: Boolean = {
    addrNum match {
      case Left(value) => value >= 3758096384L && value <= 4026531839L
      case Right(value) =>
        value >= BigInt("338953138925153547590470800371487866880") &&
          value <= BigInt("340282366920938463463374607431768211455")
    }
  }
  lazy val isUnspecified: Boolean = {
    addrNum match {
      case Left(value) => value == 0
      case Right(value) => value == 0
    }
  }
  lazy val isUniqueLocal: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= BigInt("334965454937798799971759379190646833152") &&
        value <= BigInt("337623910929368631717566993311207522303")
    }
  }
  lazy val isIPv4Mapped: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= 281470681743360L &&
          value <= 281474976710655L
    }
  }
  lazy val isIPv4Translated: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= BigInt("18446462598732840960") &&
          value <= BigInt("18446462603027808255")
    }
  }
  lazy val isIPv4IPv6Translated: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= BigInt("524413980667603649783483181312245760") &&
          value <= BigInt("524413980667603649783483185607213055")
    }
  }
  lazy val isTeredo: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= BigInt("42540488161975842760550356425300246528") &&
          value <= BigInt("42540488241204005274814694018844196863")
    }
  }
  lazy val is6to4: Boolean = {
    addrNum match {
      case Left(_) => false
      case Right(value) =>
        value >= BigInt("42545680458834377588178886921629466624") &&
          value <= BigInt("42550872755692912415807417417958686719")
    }
  }
  lazy val isReserved: Boolean = {
    isUnspecified || isLoopback || isIPv4Mapped || isIPv4Translated || isIPv4IPv6Translated || isTeredo ||
      is6to4 || isUniqueLocal || isLinkLocal || isMulticast || isPrivate
    addrNum match {
      case Left(value) =>
        (value >= 0L && value <= 16777215L) ||
          (value >= 1681915904L && value <= 1686110207L) ||
          (value >= 3221225472L && value <= 3221225727L) ||
          (value >= 3221225984L && value <= 3221226239L) ||
          (value >= 3227017984L && value <= 3227018239L) ||
          (value >= 3323068416L && value <= 3323199487L) ||
          (value >= 3325256704L && value <= 3325256959L) ||
          (value >= 3405803776L && value <= 3405804031L) ||
          (value >= 4026531840L && value <= 4294967294L) ||
          (value == 4294967295L)
      case Right(value) =>
        (value >= BigInt("1329227995784915872903807060280344576") && value <= BigInt("1329227995784915891350551133989896191")) ||
          (value >= BigInt("42540490697277043217009159418706657280") && value <= BigInt("42540491964927643445238560915409862655")) ||
          (value >= BigInt("42540766411282592856903984951653826560") && value <= BigInt("42540766490510755371168322545197776895"))
    }
  }

  // Return network address of IP address
  def mask(maskIP: Int): IPAddress = {
    addrNum match {
      case Left(value) =>
        require(maskIP >= 0 && maskIP <= 32, "Can only mask 0-32.")
        numToIP(0xFFFFFFFF << (32 - maskIP) & value)
      case Right(value) =>
        require(maskIP >= 0 && maskIP <= 128, "Can only mask 0-128.")
        numToIP(BigInt("340282366920938463463374607431768211455") << (128 - maskIP) & value)
    }
  }
  def mask(maskIP: String): IPAddress = {
    addrNum match {
      case Left(value) =>
        require(isNetworkAddressInternal(maskIP, IPv4SubnetToCIDR(maskIP)), "Mask IP address is invalid.")
        numToIP(IPv4ToLong(maskIP) & value)
      case Right(_) => throw new Exception("Can't mask IPv6 using dotted decimal notation.")
    }
  }

}
