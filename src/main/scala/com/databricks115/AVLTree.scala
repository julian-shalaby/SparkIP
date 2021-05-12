package com.databricks115

private case class Node(network: Either[IPv4Network, IPv6Network]) {
  val value: Either[IPv4Network, IPv6Network] = network
  val left: Option[Either[IPv4Network, IPv6Network]] = None
  val right: Option[Either[IPv4Network, IPv6Network]] = None
  val height: Int = 1
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

  private def getHeight(root: Node): Int = {
    if (root == null) return 0
    root.height
  }
}
