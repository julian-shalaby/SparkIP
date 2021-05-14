package com.databricks115

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

case class Node(network: IPNetwork) {
  var value: IPNetwork = network
  var left: Node = _
  var right: Node = _
  var height: Int = 1
}

case class AVLTree() {
  private def compareNetworks(net1: IPNetwork, net2: IPNetwork): Int = {
    if (net1.networkAddress > net2.networkAddress) 1
    else if (net1.networkAddress < net2.networkAddress) -1
    else {
      if (net1.broadcastAddress > net2.broadcastAddress) 1
      else if (net1.broadcastAddress < net2.broadcastAddress) -1
      else {
        (net1.addrNumStart, net2.addrNumStart) match {
          case (Left(_), Left(_)) => 0
          case (Right(_), Right(_)) => 0
          case (Right(_), Left(_)) => 1
          case (Left(_), Right(_)) => -1
        }
      }
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

  def insert(root: Node, key: IPNetwork): Node = {
    if (root == null) return Node(key)
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

  def delete(root: Node, key: IPNetwork): Node = {
    if (root == null) return root
    else if (compareNetworks(key, root.value) == -1) root.left = delete(root.left, key)
    else if (compareNetworks(key, root.value) == 1) root.right = delete(root.right, key)
    else {
      if (root.left == null) return root.right
      else if (root.right == null) return root.left

      val temp = getMinValueNode(root.right)
      root.value = temp.value
      root.right = delete(root.right, temp.value)
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
    println(root.value)
    preOrder(root.left)
    preOrder(root.right)
  }

  @tailrec
  private def networkSearch(root: Node, key: IPNetwork): Boolean = {
    if (root == null) return false
        if (compareNetworks(key, root.value) == -1) networkSearch(root.left, key)
        else if (compareNetworks(key, root.value) == 1) networkSearch(root.right, key)
        else true
  }
  @tailrec
  private def addressSearch(root: Node, key: IPAddress): Boolean = {
    if (root == null) return false
    if (key < root.value.networkAddress) addressSearch(root.left, key)
    else if (key > root.value.broadcastAddress) addressSearch(root.right, key)
    else root.value.contains(key)
  }
  def contains(root: Node, key: Any): Boolean = {
    key match {
      case s: String =>
        val ip = try {
          Some(IPAddress(s))
        } catch {
          case _: Throwable => None
        }
        val net = try {
          Some(IPNetwork(s))
        } catch {
          case _: Throwable => None
        }
        if (ip.isDefined) addressSearch(root, ip.get)
        else if (net.isDefined) networkSearch(root, net.get)
        else throw new Exception("Bad input.")

      case ip: IPAddress => addressSearch(root, ip)
      case net: IPNetwork => networkSearch(root, net)
    }
  }

  def returnAll(root: Node): ArrayBuffer[Any] = {
    if (root == null) return null
    var temp = root
    val returnList = ArrayBuffer[Any]()
    while (temp != null) {
      returnList += temp.value

      if (temp.left != null) temp = temp.left
      else if (temp.right != null) temp = temp.right
      else temp = null
    }
    returnList
  }

  def netIntersect(root: Node, set2: IPSet): ArrayBuffer[Any] = {
    if (root == null) return null
    var temp = root
    val intersectList = ArrayBuffer[Any]()
    while (temp != null) {
      if (set2.contains(temp.value)) intersectList += temp.value
      if (temp.left != null) temp = temp.left
      else if (temp.right != null) temp = temp.right
      else temp = null
    }
    intersectList
  }
}
