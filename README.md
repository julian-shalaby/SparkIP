[![license](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://github.com/jshalaby510/SparkIP/blob/main/LICENSE)

# SparkIP
An API for working with IP addresses in Apache Spark.

## Usage
(Our installation stuff)

## License
This project is licensed under the Apache License. Please see [LICENSE](LICENSE) file for more details.

## Tutorial
### Initialize
Before using in SparkSQL, initialize SparkIP by passing `spark` to `SparkIP`.
<br/>
Optionally pass the log level as well (if left unspecified, `SparkIP` resets
the log level to "WARN" and gives a warning message).
```scala
// Import statements

val spark: SparkSession = SparkSession.builder()
  .appName("IPAddress")
  .config("spark.master", "local")
  .getOrCreate()

val schema: StructType = StructType(Array(StructField("IPAddress", StringType, nullable = false)))
val path = "ipMixedFile.json"
val ipDF: DataFrame = spark.read.schema(schema).json(path)
ipDF.createOrReplaceTempView("IPAddresses")

SparkIP(spark)
// or SparkIP(spark, "DEBUG"), SparkIP(spark, "FATAL"), etc if specifying a log level

```

### Functions
**Check address type**
```scala
// Multicast
spark.sql("SELECT * FROM IPAddresses WHERE isMulticast(IPAddress)")
ipDF.select("*").filter(isMulticast(col("IPAddress")))
ipDF.select("*").filter("isMulticast(IPAddress)")

"""
Other address types:
    isPrivate, isGlobal, isUnspecified, isReserved, 
    isLoopback, isLinkLocal, isIPv4Mapped, is6to4, 
    isTeredo, isIPv4, isIPv6
"""
```

**Sort IP Addresses**
```scala
// SparkSQL doesn't support values > LONG_MAX
// To sort IPv6 addresses, use ipAsBinary
// To sort IPv4 addresses, use either ipv4AsNum or ipAsBinary, but ipv4AsNum is more efficient

// Sort IPv4 and IPv6
spark.sql("SELECT * FROM IPAddresses SORT BY ipAsBinary(IPAddress)")
ipDF.select('*').sort(ipAsBinary(col("IPAddress")))

// Sort ONLY IPv4
spark.sql("SELECT * FROM IPv4 SORT BY ipv4AsNum(IPAddress)")
ipv4DF.select('*').sort(ipv4AsNum(col("IPAddress")))
```

**IP network functions**
```scala
// Network contains
spark.sql("SELECT * FROM IPAddresses WHERE networkContains(IPAddress, '195.0.0.0/16')")

val net1 = "192.0.0.0/16"
ipDF.select("*").filter(netContains(net1)(col("IPAddress")))

// or use IPNetwork objects
val net2 = IPNetwork("192.0.0.0/16")
ipDF.select("*").filter(netContains(net2)(col("IPAddress")))
```

**IP Set**
#### Create IP Sets (Note: This functionality also works with add and remove):
```scala
// Strings
val ipStr = "192.0.0.0"
val netStr = "225.0.0.0"
// Collections
val ip_net_mix = Set("::5", "5.0.0.0/8", "111.8.9.7")
// IPAddress/IPNetwork objects
val ipAddr = IPAddress("::")

/*
Or use our predefined networks (multicastIPs, privateIPs, 
 publicIPs, reservedIPs, unspecifiedIPs, linkLocalIPs, 
 loopBackIPs, ipv4MappedIPs, ipv4TranslatedIPs, ipv4ipv6TranslatedIPs,
 teredoIPs, sixToFourIPs)
 */

// Mix them together
val ipSet = IPSet(ipStr, "::/16", "2001::", netStr, ip_net_mix, privateIPs)
val ipSet2 = IPSet("6::", "9.0.8.7", ipAddr)
// Use other IPSets
val ipSet3 = IPSet(ipSet, ipSet2)
// Or just make an empty set
val ipSet4 = IPSet()
```
#### Register IP Sets for use in SparkSQL:
Before using IP Sets in SparkSQL, register it by passing it to `SparkIP`
```scala
val ipSet = IPSet("::")
val ipSet2 = IPSet()

// Pass the set, then the set name
SparkIP.add(ipSet, "ipSet")
SparkIP.add(ipSet2, "ipSet2")
```
#### Remove IP Sets from registered sets in SparkSQL:
```scala
SparkIP.remove("ipSet", "ipSet2")
```

#### Use IP Sets in SparkSQL:
```scala
// Note you have to pass the variable name using SparkSQL, not the actual variable

// Initialize an IP Set
val setOfIPs = Set("192.0.0.0", "5422:6622:1dc6:366a:e728:84d4:257e:655a", "::")
val ipSet = IPSet(setOfIPs)

// Register it
SparkIP.add(ipSet, "ipSet")

// Use it!
// Set Contains
spark.sql("SELECT * FROM IPAddresses WHERE setContains(IPAddress, 'ipSet')")
ipDF.select('*').filter("setContains(IPAddress, 'ipSet')")
ipDF.select('*').withColumn("setCol", setContains(ipSet)(col("IPAddress")))

// Show sets available to use
SparkIP.setsAvailable()

// Remove a set
SparkIP.remove("ipSet")

// Clear sets available
SparkIP.clear()
```

#### IP Set functions (outside SparkSQL):
```scala
val ipSet = IPSet()

// Add
ipSet.add("0.0.0.0", "::/16")

// Remove
ipSet.remove("::/16")

// Contains
ipSet.contains("0.0.0.0")

// Clear
ipSet.clear()

// Show all
ipSet.showAll()

// Union
val ipSet2 = ("2001::", "::33", "ffff::f")
ipSet.union(ipSet2)

// Intersection
ipSet.intersects(ipSet2)

// Diff
ipSet.diff(ipSet2)

// Show All
ipSet.showAll()

// Return All
ipSet.returnAll()

// Is empty
ipSet.isEmpty()

// Compare IPSets
ipSet2 = ("2001::", "::33", "ffff::f")
ipSet == ipSet2
ipSet != ipSet2

// Return the # of elements in the set
ipSet.length
```