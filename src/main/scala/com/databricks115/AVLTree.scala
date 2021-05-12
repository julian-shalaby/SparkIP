package com.databricks115

import scala.annotation.tailrec

case class Node(network: Either[IPv4Network, IPv6Network]) {
  val value: Either[IPv4Network, IPv6Network] = network
  var left: Node = _
  var right: Node = _
  var height: Int = 1
}

case class AVLTree() {
  private def compareNetworks(net1: IPv4Network, net2: IPv4Network): Int = {
    if (net1.networkAddress > net2.networkAddress) 1
    else if (net1.networkAddress < net2.networkAddress) -1
    else {
      if (net1.broadcastAddress > net2.broadcastAddress) 1
      else if (net1.broadcastAddress < net2.broadcastAddress) -1
      else 0
    }
  }
  private def compareNetworks(net1: IPv4Network, net2: IPv6Network): Int = {
    if (net1.networkAddress > net2.networkAddress) 1
    else if (net1.networkAddress < net2.networkAddress) -1
    else {
      if (net1.broadcastAddress > net2.broadcastAddress) 1
      else if (net1.broadcastAddress < net2.broadcastAddress) -1
      else -1
    }
  }
  private def compareNetworks(net1: IPv6Network, net2: IPv4Network): Int = {
    if (net1.networkAddress > net2.networkAddress) 1
    else if (net1.networkAddress < net2.networkAddress) -1
    else {
      if (net1.broadcastAddress > net2.broadcastAddress) 1
      else if (net1.broadcastAddress < net2.broadcastAddress) -1
      else 1
    }
  }
  private def compareNetworks(net1: IPv6Network, net2: IPv6Network): Int = {
    if (net1.networkAddress > net2.networkAddress) 1
    else if (net1.networkAddress < net2.networkAddress) -1
    else {
      if (net1.broadcastAddress > net2.broadcastAddress) 1
      else if (net1.broadcastAddress < net2.broadcastAddress) -1
      else 0
    }
  }
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
        else 0
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
        else 0
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

  def preOrder(root: Node): Unit = {
    if (root == null) return
    println(root.value)
    preOrder(root.left)
    preOrder(root.right)
  }
}

object AVLTest extends App {
  val hi = AVLTree()
  var root = Node(Left(IPv4Network("0.0.0.0/16")))
  root = hi.insert(root, IPv4Network("192.0.0.0/16"))
  root = hi.insert(root, IPv6Network("::/16"))
  root = hi.insert(root, IPv6Network("2001::/16"))
  root = hi.insert(root, IPv6Network("2001::/16"))
  root = hi.insert(root, IPv4Network("255.0.0.0/16"))

  hi.preOrder(root)
}
