package com.databricks115

case class IPNetwork(network: String) extends IPTraits with Ordered[IPNetwork]{
  // If input is in range format
  private var IP2: Option[String] = None

  // Parse the network
  private val (addr: String, cidr: Int) = {
    val cidrSplit = network.split('/')
    lazy val rangeSplit = network.split('-')

    if (cidrSplit.length == 2) {
      if (cidrSplit(1).length <= 3) {
        val cidrBlock = cidrSplit(1).toInt
        require(isNetworkAddress(cidrSplit(0), cidrBlock), "IP address must be the network address.")
        require(cidrBlock >= 0 && cidrBlock <= 128, "Bad Network CIDR.")
        (cidrSplit(0), cidrBlock)
      }
      else {
        val cidrBlock = IPv4SubnetToCIDR(cidrSplit(1))
        require(isNetworkAddress(cidrSplit(1), cidrBlock), "IP address must be the network address.")
        require(isNetworkAddress(cidrSplit(0), cidrBlock), "Dotted decimal IP address is invalid.")
        (cidrSplit(0), cidrBlock)
      }
    }

    else if (rangeSplit.length == 2) {
      IP2 = Some(rangeSplit(1))
      (rangeSplit(0), -1)
    }

    else throw new Exception("Bad Network.")
  }

  // Start and end of the network
  val (addrNumStart: Either[Long, BigInt], addrNumEnd: Either[Long, BigInt]) = {
    val addrNum = IPToNum(addr)
    addrNum match {
      case Left(value) =>
        (if (IP2.isDefined) Left(value) else Left(0xFFFFFFFF << (32 - cidr) & value),
          if (IP2.isDefined) {
            val temp = IPToNum(IP2.getOrElse(throw new Exception("Bad IP Network Range.")))
            require(temp.left.getOrElse(None) != None && temp.left.get > value, "Bad IP Network Range.")
            temp
          }
          else {
            require(cidr <= 32, "Bad network CIDR.")
            Left(value | ((1 << (32 - cidr)) - 1))
          })
      case Right(value) =>
        (if (IP2.isDefined) Right(value) else Right(BigInt("340282366920938463463374607431768211455") << (128-cidr) & value),
          if (IP2.isDefined) {
            val temp = IPToNum(IP2.getOrElse(throw new Exception("Bad IP Network Range.")))
            require(temp.right.getOrElse(None) != None && temp.right.get > value, "Bad IP Network Range.")
            temp
          }
          else Right(value | ((BigInt(1) << (128 - cidr)) - 1))
        )
    }
  }
  val version: Int = addrNumStart match {
    case Left(_) => 4
    case Right(_) => 6
  }

  // Range of the network
  lazy val range: String = {
    addrNumStart match {
      case Left(value) => s"${numToIP(value)}-${numToIP(addrNumEnd.left.get)}"
      case Right(value) => s"${numToIP(value)}-${numToIP(addrNumEnd.right.get)}"
    }
  }

  // Access operators
  lazy val networkAddress: IPAddress = {
    addrNumStart match {
      case Left(value) => numToIP(value)
      case Right(value) => numToIP(value)
    }
  }
  lazy val broadcastAddress: IPAddress = {
    addrNumEnd match {
      case Left(value) => numToIP(value)
      case Right(value) => numToIP(value)
    }
  }

  // Compare networks
  def ==(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) => value1 == value2 && this.addrNumEnd.left.get == that.addrNumEnd.left.get
      case (Right(value1), Right(value2)) => value1 == value2 && this.addrNumEnd.right.get == that.addrNumEnd.right.get
      case _ => false
    }
  }
  def !=(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) => value1 != value2 || this.addrNumEnd.left.get != that.addrNumEnd.left.get
      case (Right(value1), Right(value2)) => value1 != value2 || this.addrNumEnd.right.get != that.addrNumEnd.right.get
      case _ => true
    }
  }
  override def <(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) =>
        value1 < value2 ||
        (value1 == value2 && this.addrNumEnd.left.get < that.addrNumEnd.left.get)
      case (Right(value1), Right(value2)) =>
        value1 < value2 ||
          (value1 == value2 && this.addrNumEnd.right.get < that.addrNumEnd.right.get)
      case (Left(_), Right(_)) => true
      case (Right(_), Left(_)) => false
    }
  }
  override def >(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) =>
        value1 > value2 ||
          (value1 == value2 && this.addrNumEnd.left.get > that.addrNumEnd.left.get)
      case (Right(value1), Right(value2)) =>
        value1 > value2 ||
          (value1 == value2 && this.addrNumEnd.right.get > that.addrNumEnd.right.get)
      case (Left(_), Right(_)) => false
      case (Right(_), Left(_)) => true
    }
  }
  override def <=(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) =>
        value1 < value2 ||
          (value1 == value2 && this.addrNumEnd.left.get < that.addrNumEnd.left.get) ||
          (value1 == value2 && this.addrNumEnd.left.get == that.addrNumEnd.left.get)
      case (Right(value1), Right(value2)) =>
        value1 < value2 ||
          (value1 == value2 && this.addrNumEnd.right.get < that.addrNumEnd.right.get) ||
          (value1 == value2 && this.addrNumEnd.right.get == that.addrNumEnd.right.get)
      case (Left(_), Right(_)) => true
      case (Right(_), Left(_)) => false
    }
  }
  override def >=(that: IPNetwork): Boolean = {
    (this.addrNumStart, that.addrNumStart) match {
      case (Left(value1), Left(value2)) =>
        value1 > value2 ||
          (value1 == value2 && this.addrNumEnd.left.get > that.addrNumEnd.left.get) ||
          (value1 == value2 && this.addrNumEnd.left.get == that.addrNumEnd.left.get)
      case (Right(value1), Right(value2)) =>
        value1 > value2 ||
          (value1 == value2 && this.addrNumEnd.right.get > that.addrNumEnd.right.get) ||
          (value1 == value2 && this.addrNumEnd.right.get == that.addrNumEnd.right.get)
      case (Left(_), Right(_)) => false
      case (Right(_), Left(_)) => true
    }
  }
  def compare(that: IPNetwork): Int = {
    if (this == that) 0
    else if (this < that) -1
    else 1
  }

  // Checks if an IP is in the network
  def contains(ip: Any): Boolean = ip match {
    case addr: IPAddress =>
      (addr.addrNum, addrNumStart) match {
        case (Left(value1), Left(value2)) => value1 >= value2 && value1 <= addrNumEnd.left.get
        case (Right(value1), Right(value2)) => value1 >= value2 && value1 <= addrNumEnd.right.get
        case _ => false
      }
    case str: String =>
      try {
        contains(IPAddress(str))
      }
      catch {
        case _: Throwable => throw new Exception("Bad IP address.")
      }
    case _ => false
  }

  // Checks if networks overlap
  def netsIntersect(net: IPNetwork): Boolean = {
    (this.addrNumStart, net.addrNumStart) match {
      case (Left(value1), Left(value2)) => value1 <= net.addrNumEnd.left.get && this.addrNumEnd.left.get >= value2
      case (Right(value1), Right(value2)) => value1 <= net.addrNumEnd.right.get && this.addrNumEnd.right.get >= value2
      case _ => false
    }
  }

}
