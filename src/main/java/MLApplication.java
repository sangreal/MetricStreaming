import com.fasterxml.jackson.databind.ObjectMapper;
import entity.JoinWrapper;
import entity.MergeEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.collection.immutable.Stream;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MLApplication {
   public void startProcess() {
       Map<String, Object> params1 = new HashMap<>();
       params1.put("bootstrap.servers", Constants.kafkaServer);
       params1.put("key.deserializer", StringDeserializer.class);
       params1.put("value.deserializer", StringDeserializer.class);
       params1.put("group.id", "ml_streaming");
       params1.put("auto.offset.reset", "latest");
       params1.put("enable.auto.commit", false);


       SparkConf sparkConf = new SparkConf().setAppName("JoinedStreaming").setMaster("local[4]");
       JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(2000));
       SparkSession sparkSession = SparkSession
               .builder()
               .appName("kafka-streaming")
               .master("local")
               .getOrCreate();

//       private double cpu_pct;
//       private double mem_pct;
//       private String container_name;
//       private String message;
//       private Date timestamp;
       StructType mergeStruct = new StructType()
               .add("cpu_pct", DataTypes.DoubleType)
               .add("mem_pct", DataTypes.DoubleType)
               .add("container_name", DataTypes.StringType)
               .add("message", DataTypes.StringType)
               .add("timestamp", DataTypes.DateType);

       Dataset<Row> df = sparkSession
               .readStream()
               .format("kafka")
               .option("subscribe", Constants.joinTopic)
               .option("kafka.bootstrap.servers", Constants.kafkaServer)
               .option("startingOffsets", "latest")
               .load();

       try {
           df.selectExpr("CAST(value AS STRING)")
                   .withColumnRenamed("value", "line")
                   .select(functions.from_json(new Column("line"), mergeStruct).as("merge_entity"))
                   .selectExpr("merge_entity.*")
                   .as(Encoders.bean(MergeEntity.class))
                   .printSchema();
       df.selectExpr("CAST(value AS STRING)")
               .withColumnRenamed("value", "line")
               .select(functions.from_json(new Column("line"), mergeStruct).as("merge_entity"))
               .selectExpr("merge_entity.*")
               .as(Encoders.bean(MergeEntity.class))
               .writeStream()
               .format("console")
               .option("truncate","false")
               .start()
               .awaitTermination();

//       ds.printSchema();




           ssc.start();
           ssc.awaitTermination();
       } catch (InterruptedException | StreamingQueryException e) {
           e.getMessage();
       }

   }

   public static void main(String[] args) {
       MLApplication mlApplication = new MLApplication();
       mlApplication.startProcess();
   }
}
