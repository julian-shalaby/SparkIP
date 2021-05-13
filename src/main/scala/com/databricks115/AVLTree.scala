package com.databricks115

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

case class Node(network: Either[IPv4Network, IPv6Network]) {
  var value: Either[IPv4Network, IPv6Network] = network
  var left: Node = _
  var right: Node = _
  var height: Int = 1
}

case class AVLTree() {
  private def compareNetworks(net1: IPv4Network, net2: Either[IPv4Network, IPv6Network]): Int = {
    if (net2 == null) return -1
    net2 match {
      case Left(value) => if (net1.networkAddress > value.networkAddress) 1
      else if (net1.networkAddress < value.networkAddress) -1
      else {
        if (net1.broadcastAddress > value.broadcastAddress) 1
        else if (net1.broadcastAddress < value.broadcastAddress) -1
        else 0
      }
      case Right(value) => if (net1.networkAddress > value.networkAddress) 1
      else if (net1.networkAddress < value.networkAddress) -1
      else {
        if (net1.broadcastAddress > value.broadcastAddress) 1
        else if (net1.broadcastAddress < value.broadcastAddress) -1
        else -1
      }
    }
  }
  private def compareNetworks(net1: IPv6Network, net2: Either[IPv4Network, IPv6Network]): Int = {
    if (net2 == null) return -1
    net2 match {
      case Left(value) => if (net1.networkAddress > value.networkAddress) 1
      else if (net1.networkAddress < value.networkAddress) -1
      else {
        if (net1.broadcastAddress > value.broadcastAddress) 1
        else if (net1.broadcastAddress < value.broadcastAddress) -1
        else 1
      }
      case Right(value) => if (net1.networkAddress > value.networkAddress) 1
      else if (net1.networkAddress < value.networkAddress) -1
      else {
        if (net1.broadcastAddress > value.broadcastAddress) 1
        else if (net1.broadcastAddress < value.broadcastAddress) -1
        else 0
      }
    }
  }
  private def compareNetworks(net1: Any, net2: Either[IPv4Network, IPv6Network]): Int = {
    if (net2 == null) return -1
    net1 match {
      case v4Net: IPv4Network => compareNetworks(v4Net, net2)
      case v6Net: IPv6Network => compareNetworks(v6Net, net2)
    }
  }

  private def getHeight(root: Node): Int = {
    if (root == null) return 0
    root.height
  }

  private def getBalance(root: Node): Int = {
    if (root == null) return 0
    getHeight(root.left) - getHeight(root.right)
  }

  @tailrec
  private def getMinValueNode(root: Node): Node = {
    if (root == null || root.left == null) return root
    getMinValueNode(root)
  }

  private def leftRotate(z: Node): Node = {
    val y = z.right
    val T2 = y.left

    y.left = z
    z.right = T2

    z.height = 1 + getHeight(z.left).max(getHeight(z.right))
    y.height = 1 + getHeight(y.left).max(getHeight(y.right))

    y
  }

  private def rightRotate(z: Node): Node = {
    val y = z.left
    val T3 = y.right

    y.right = z
    z.left = T3

    z.height = 1 + getHeight(z.left).max(getHeight(z.right))
    y.height = 1 + getHeight(y.left).max(getHeight(y.right))

    y
  }

  def insert(root: Node, key: IPv4Network): Node = {
    if (root == null) return Node(Left(key))
    else if (compareNetworks(key, root.value) == -1) root.left = insert(root.left, key)
    else if (compareNetworks(key, root.value) == 1) root.right = insert(root.right, key)
    else return root

    root.height = 1 + getHeight(root.left).max(getHeight(root.right))

    val balance = getBalance(root)
    if (balance > 1 && compareNetworks(key, root.left.value) == -1) return rightRotate(root)
    if (balance < -1 && compareNetworks(key, root.right.value) == 1) return leftRotate(root)
    if (balance > 1 && compareNetworks(key, root.left.value) == 1) {
      root.left = leftRotate(root.left)
      return rightRotate(root)
    }
    if (balance < -1 && compareNetworks(key, root.right.value) == -1) {
      root.right = rightRotate(root.right)
      return leftRotate(root)
    }

    root
  }

  def insert(root: Node, key: IPv6Network): Node = {
    if (root == null) return Node(Right(key))
    else if (compareNetworks(key, root.value) == -1) root.left = insert(root.left, key)
    else if (compareNetworks(key, root.value) == 1) root.right = insert(root.right, key)
    else return root

    root.height = 1 + getHeight(root.left).max(getHeight(root.right))

    val balance = getBalance(root)
    if (balance > 1 && compareNetworks(key, root.left.value) == -1) return rightRotate(root)
    if (balance < -1 && compareNetworks(key, root.right.value) == 1) return leftRotate(root)
    if (balance > 1 && compareNetworks(key, root.left.value) == 1) {
      root.left = leftRotate(root.left)
      return rightRotate(root)
    }
    if (balance < -1 && compareNetworks(key, root.right.value) == -1) {
      root.right = rightRotate(root.right)
      return leftRotate(root)
    }

    root
  }

  def delete(root: Node, key: IPv4Network): Node = {
    if (root == null) return root
    else if (compareNetworks(key, root.value) == -1) root.left = delete(root.left, key)
    else if (compareNetworks(key, root.value) == 1) root.right = delete(root.right, key)
    else {
      if (root.left == null) return root.right
      else if (root.right == null) return root.left

      val temp = getMinValueNode(root.right)
      root.value = temp.value
      root.right = delete(root.right, temp.value.left.get)
    }

    if (root == null) return root

    root.height = 1 + getHeight(root.left).max(getHeight(root.right))

    val balance = getBalance(root)
    if (balance > 1 && getBalance(root.left) >= 0) return rightRotate(root)
    if (balance < -1 && getBalance(root.right) <= 0) return leftRotate(root)
    if (balance > 1 && getBalance(root.left) < 0) {
      root.left = leftRotate(root.left)
      return rightRotate(root)
    }
    if (balance < -1 && getBalance(root.right) > 0) {
      root.right = rightRotate(root.right)
      return leftRotate(root)
    }

    root
  }
  def delete(root: Node, key: IPv6Network): Node = {
    if (root == null) return root
    else if (compareNetworks(key, root.value) == -1) root.left = delete(root.left, key)
    else if (compareNetworks(key, root.value) == 1) root.right = delete(root.right, key)
    else {
      if (root.left == null) return root.right
      else if (root.right == null) return root.left

      val temp = getMinValueNode(root.right)
      root.value = temp.value
      root.right = delete(root.right, temp.value.left.get)
    }

    if (root == null) return root

    root.height = 1 + getHeight(root.left).max(getHeight(root.right))

    val balance = getBalance(root)
    if (balance > 1 && getBalance(root.left) >= 0) return rightRotate(root)
    if (balance < -1 && getBalance(root.right) <= 0) return leftRotate(root)
    if (balance > 1 && getBalance(root.left) < 0) {
      root.left = leftRotate(root.left)
      return rightRotate(root)
    }
    if (balance < -1 && getBalance(root.right) > 0) {
      root.right = rightRotate(root.right)
      return leftRotate(root)
    }

    root
  }

  def preOrder(root: Node): Unit = {
    if (root == null) return
    root.value match {
      case Left(value) => println(value)
      case Right(value) => println(value)
    }
    preOrder(root.left)
    preOrder(root.right)
  }

  def AVLSearch(root: Node, key: Any): Boolean = {
    if (root == null) return false
      key match {
        case v4Net: IPv4Network =>
          if (compareNetworks(v4Net, root.value) == -1) AVLSearch(root.left, v4Net)
          else if (compareNetworks(v4Net, root.value) == 1) AVLSearch(root.right, v4Net)
          else true
        case v6Net: IPv6Network =>
          if (compareNetworks(v6Net, root.value) == -1) AVLSearch(root.left, v6Net)
          else if (compareNetworks(v6Net, root.value) == 1) AVLSearch(root.right, v6Net)
          else true
        case s: String =>
          val v4Net = try {
            Some(IPv4Network(s))
          }
          catch {
            case _: Throwable => None
          }
          val v6Net = try {
            Some(IPv6Network(s))
          }
          catch {
            case _: Throwable => None
          }
          if (v4Net.isDefined) AVLSearch(root, v4Net.get)
          else if (v6Net.isDefined) AVLSearch(root, v6Net.get)
          else false
        case _ => false
      }
  }
  def AVLSearchIP(root: Node, key: Any): Boolean = {
    if (root == null) return false
    key match {
      case v4: IPv4 =>
        root.value match {
          case Left(value) =>
            if (v4.addrL < value.networkAddress.addrL) AVLSearchIP(root.left, key)
            else if (v4.addrL > value.broadcastAddress.addrL) AVLSearchIP(root.right, key)
            else value.contains(v4)
          case Right(value) =>
            if (v4.addrL < value.networkAddress.addrBI) AVLSearchIP(root.left, key)
            else if (v4.addrL > value.broadcastAddress.addrBI) AVLSearchIP(root.right, key)
            else value.contains(v4)
          case _ => false
        }
      case v6: IPv6 =>
        root.value match {
          case Left(value) =>
            if (v6.addrBI < value.networkAddress.addrL) AVLSearchIP(root.left, key)
            else if (v6.addrBI > value.broadcastAddress.addrL) AVLSearchIP(root.right, key)
            else value.contains(v6)
          case Right(value) =>
            if (v6.addrBI < value.networkAddress.addrBI) AVLSearchIP(root.left, key)
            else if (v6.addrBI > value.broadcastAddress.addrBI) AVLSearchIP(root.right, key)
            value.contains(v6)
          case _ => false
        }
      case s: String =>
        val v4 = try {
          Some(IPv4(s))
        }
        catch {
          case _: Throwable => None
        }
        val v6 = try {
          Some(IPv6(s))
        }
        catch {
          case _: Throwable => None
        }
        if (v4.isDefined) AVLSearchIP(root, v4.get)
        else if (v6.isDefined) AVLSearchIP(root, v6.get)
        else false
      case _ => false
    }
  }

  def returnAll(root: Node): ArrayBuffer[Any] = {
    if (root == null) return null
    var temp = root
    val returnList = ArrayBuffer[Any]()
    while (temp != null) {
      temp.value match {
        case Left(value) => returnList += value
        case Right(value) => returnList += value
      }
      if (temp.left != null) temp = temp.left
      else if (temp.right != null) temp = temp.right
      else temp = null
    }
    returnList
  }
}
