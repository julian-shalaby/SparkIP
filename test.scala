import spark.implicits._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

val schema = StructType(Array(StructField("ip", IPv4Type, true)))

val data = Seq(Row("10.0.0.1"))

val df = spark.createDataFrame(spark.sparkContext.parallelize(data),schema)
