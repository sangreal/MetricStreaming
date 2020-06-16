import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.SimpleMetricEntity;
import entity.metricbeat.ContainerMetrics;
import kafka.MyKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.codehaus.jackson.JsonParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SingleMetricApplication {
    private void initStreamingContext() {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("bootstrap.servers", Constants.kafkaServer);
        params1.put("key.deserializer", StringDeserializer.class);
        params1.put("value.deserializer", StringDeserializer.class);
        params1.put("group.id", "single_metricsstreaming");
        params1.put("auto.offset.reset", "latest");
        params1.put("enable.auto.commit", false);


        SparkConf sparkConf = new SparkConf().setAppName("SingleMetric").setMaster("local[4]");
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
                                && containerMetrics.getKubernetes().getPod().getName() != null
                                && containerMetrics.getEvent().getDataset().equals("kubernetes.container")) {
                            System.out.println("key : " + containerMetrics.getKubernetes().getPod().getName() + "raw value : " + rawData);
                            SimpleMetricEntity simpleMetricEntity = SimpleMetricEntity.from(containerMetrics);
                            myKafkaProducer.send(containerMetrics.getKubernetes().getPod().getName(), objectMapper.writeValueAsString(simpleMetricEntity));
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
        } catch (Exception e) {

        } finally {
            ssc.close();
        }
    }

    public static void main(String[] args) {
        SingleMetricApplication singleMetricApplication = new SingleMetricApplication();
        singleMetricApplication.initStreamingContext();
    }
}
