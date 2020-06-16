import entity.MergeEntity
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Column, SparkSession}
import org.apache.spark.sql.functions.{column, expr, from_json}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext

object MiApp {
  private val sparkConf = new SparkConf().setAppName("kafkastream").setMaster("local")

  private val sparkSession = SparkSession.builder
    .appName("kafkastream")
    .master("local")
    .getOrCreate();

  def sinkingJoinedTopic2CSV(join_topic: String = "joined2"): Unit = {
    val df = sparkSession.readStream.format("kafka")
      .option("kafka.bootstrap.servers", Constants.kafkaServer)
      .option("subscribe", Constants.joinTopic)
      .option("startingOffsets", "latest").load


    val struct_merge = new StructType()
      .add("cpu_pct", DataTypes.DoubleType)
      .add("mem_pct", DataTypes.DoubleType)

      .add("container_name", DataTypes.StringType)
      .add("message", DataTypes.StringType)
      .add("timestamp", DataTypes.LongType)



    df
      .selectExpr("CAST(value AS STRING)")
      .select(from_json(column("value"), struct_merge).as("merge"))
      .selectExpr("merge.cpu_pct", "merge.timestamp", "merge.mem_pct")
      .coalesce(1)
//      .printSchema()
      .writeStream
      .format("csv")
      .option("checkpointLocation", "/tmp/checkpoints")
      .option("header", "true")
      .option("path", "/tmp/merge_csv6")
//      .format("console")
      .start()
      .awaitTermination()

  }


  def main(args: Array[String]): Unit = {
      sinkingJoinedTopic2CSV()
  }
}
