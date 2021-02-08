package com.databricks115
import org.apache.spark.sql.types.DataType

/*
    Do something with bottom and top ip addresses of a network?
    network address and broadcast address are always unusable, but a specific network and/or broadcast addresses
    is available if it isn't the network or broadcast address of a specific network

    example: 73.231.169.178
        a) 73.231.169.178/30's network address = 73.231.169.176 and broadcast address = 73.231.169.179
        b) 73.231.169.178/16's network address = 73.231.0.0 and broadcast address = 73.231.255.255
        c) 73.231.169.176 is usable in 73.231.169.178/16, but unusable in 73.231.169.178/30

    just handle this in the IP network class?
 */

/*
    Change class name to IPv4Address if IPv4 and IPv6 will be completely separate classes?
 */

trait IPAddress extends DataType {
    override def asNullable(): DataType = return this
    override def defaultSize(): Int = return 1

    def isIP(ip: String): Boolean    

    def mask(maskIP: String): IPAddress

    val isMulticast: Boolean
    val isPrivate: Boolean
    val isGlobal: Boolean
    val isUnspecified: Boolean
    val isLoopback: Boolean
    val isLinkLocal: Boolean
    val isReserved: Boolean
}
