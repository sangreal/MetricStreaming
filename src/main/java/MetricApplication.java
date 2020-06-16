import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.rowset.internal.Row;
import entity.logbeat.ContainerLogs;
import entity.metricbeat.ContainerMetrics;
import kafka.MyKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.codehaus.jackson.JsonParseException;
import scala.Tuple2;

import java.util.*;

public class MetricApplication {


    private void initStreamingContext() {
        Map<String, Object> params1 = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params1.put("bootstrap.servers", Constants.kafkaServer);
        params1.put("key.deserializer", StringDeserializer.class);
        params1.put("value.deserializer", StringDeserializer.class);
        params1.put("group.id", "metric_streaming");
        params1.put("auto.offset.reset", "latest");
        params1.put("enable.auto.commit", false);

        params1.put("bootstrap.servers", Constants.kafkaServer);
        params1.put("key.deserializer", StringDeserializer.class);
        params1.put("value.deserializer", StringDeserializer.class);
        params1.put("group.id", "log_streaming");
        params1.put("auto.offset.reset", "latest");
        params1.put("enable.auto.commit", false);


        SparkConf sparkConf = new SparkConf().setAppName("DirectKafkaMetric").setMaster("local[4]");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(2000));

        // metricsbeat
        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Collections.singletonList(Constants.oldMetricTopic), params1));

        stream.foreachRDD(rdd -> {
            rdd.foreachPartition(record -> {
                MyKafkaProducer myKafkaProducer = new MyKafkaProducer.Builder()
                        .setBootstrapServer(Constants.kafkaServer)
                        .setTopic(Constants.newMetricTopic)
                        .build();
                ObjectMapper objectMapper = new ObjectMapper();
                while (record.hasNext()) {
                    try {

                        String rawData = record.next().value();
                        ContainerMetrics containerMetrics = objectMapper.readValue(rawData, ContainerMetrics.class);
                        System.out.println(containerMetrics.toString());
                        if (containerMetrics.getKubernetes() != null
                                && containerMetrics.getKubernetes().getPod() != null
                        && containerMetrics.getKubernetes().getPod().getName() != null) {
                            System.out.println("key : " + containerMetrics.getKubernetes().getPod().getName() + "raw value : " + rawData);
                            myKafkaProducer.send(containerMetrics.getKubernetes().getPod().getName(), rawData);
                        }
                    } catch (JsonParseException | JsonMappingException e) {
                        System.out.println("json exception ： " + e.getMessage());
                    }
                }
            });
        });

        // logbeat

        JavaInputDStream<ConsumerRecord<String, String>> stream2 = KafkaUtils.createDirectStream(ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Collections.singletonList(Constants.oldLogTopic), params1));



        stream2.foreachRDD(rdd -> {
            rdd.foreachPartition(record -> {
                MyKafkaProducer myKafkaProducer = new MyKafkaProducer.Builder()
                        .setBootstrapServer(Constants.kafkaServer)
                        .setTopic(Constants.newLogTopic)
                        .build();
                ObjectMapper objectMapper = new ObjectMapper();
                while (record.hasNext()) {
                    try {

                        String rawData = record.next().value();
                        ContainerLogs containerLogs = objectMapper.readValue(rawData, ContainerLogs.class);
                        System.out.println(containerLogs.toString());
                        if ( containerLogs.getKubernetes() != null
                                && containerLogs.getKubernetes().getPod() != null) {
                            System.out.println("key : " + containerLogs.getKubernetes().getPod().getName() + "raw value : " + rawData);
                            myKafkaProducer.send(containerLogs.getKubernetes().getPod().getName(), rawData);
                        }
                    } catch (JsonParseException | JsonMappingException e) {
                        System.out.println("json exception ： " + e.getMessage());
                    }
                }
            });
        });

        try {

        ssc.start();
            ssc.awaitTermination();
        } catch (InterruptedException e) {
            e.getMessage();
        }

    }

    private MyKafkaProducer buildProducer(String topic, String kafkaServer) {
        return new MyKafkaProducer.Builder()
                .setBootstrapServer(kafkaServer)
                .setTopic(topic)
                .build();
    }


    public static void main(String[] args) {
        MetricApplication metricApplication = new MetricApplication();
        metricApplication.initStreamingContext();
    }



}
