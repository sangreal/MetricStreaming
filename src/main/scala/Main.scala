import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit = {
//
//    val kafkaParams = Map[String, Object](
//      "bootstrap.servers" -> "10.176.13.8:9092",
//      "key.deserializer" -> classOf[StringDeserializer],
//      "value.deserializer" -> classOf[StringDeserializer],
//      "group.id" -> "use_a_separate_group_id_for_each_stream",
//      "auto.offset.reset" -> "latest",
//      "enable.auto.commit" -> (false: java.lang.Boolean)
//    )
//
//    val sparkConf = new SparkConf().setAppName("DirectKafka").setMaster("local[4]");
//    val ssc = new StreamingContext(sparkConf, Seconds(2))
//    val topics = Array("metric_topic")
//    val stream = KafkaUtils.createDirectStream[String, String](
//      ssc,
//      LocationStrategies.PreferConsistent,
//      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
//    )

//    stream.foreachRDD(rdd =>
//    rdd.foreachPartition(record => record.foreach(record => record))
//    ssc.start()
//    ssc.awaitTermination()

    val kmeansApp = new KmeansApp()
    kmeansApp.start()

  }
}
