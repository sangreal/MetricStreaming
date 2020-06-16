import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{column, from_json}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{DataTypes, StructType}

object SingleMetricsApp {
  private val sparkConf = new SparkConf().setAppName("kafkastream").setMaster("local")

  private val sparkSession = SparkSession.builder
    .appName("singleMetricsStream")
    .master("local")
    .getOrCreate();

  def sinkingJoinedTopic2CSV(join_topic: String = Constants.newMetricTopic): Unit = {
    val df = sparkSession.readStream.format("kafka")
      .option("kafka.bootstrap.servers", Constants.kafkaServer)
      .option("subscribe", join_topic)
      .option("startingOffsets", "latest").load


    val struct_merge = new StructType()
      .add("cpu_pct", DataTypes.DoubleType)
      .add("mem_pct", DataTypes.DoubleType)

      .add("container_name", DataTypes.StringType)
      .add("timestamp", DataTypes.LongType)



    df
      .selectExpr("CAST(value AS STRING)")
      .select(from_json(column("value"), struct_merge).as("metrics"))
      .selectExpr("metrics.cpu_pct", "metrics.timestamp", "metrics.mem_pct")
      .coalesce(1)
//            .printSchema()
      .writeStream
//      .foreachBatch((batchDF: DataFrame, batchId: Long) => {
//        batchDF.write
//          .format("com.databricks.spark.csv")
//          .option("header", "true")
//          .mode("append")
//          .partitionBy("timestamp")
//          .save("/tmp/csv2/metric2")
//      })
      .format("csv")
      .option("checkpointLocation", "/tmp/checkpoints")
      .option("header", "true")
      .option("path", "/tmp/csv4/")
      .trigger(Trigger.ProcessingTime("10 minutes"))
//            .format("console")
      .start()
      .awaitTermination()

  }


  def main(args: Array[String]): Unit = {
    sinkingJoinedTopic2CSV()
  }
}
