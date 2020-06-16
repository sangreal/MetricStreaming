import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.JoinWrapper;
import entity.MergeEntity;
import entity.logbeat.ContainerLogs;
import entity.metricbeat.ContainerMetrics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KStreamApplication {
    private static final String kafkaServer = "10.176.13.8:9092";

    private void init() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "metric-streaming");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> metricstream = builder.stream(Constants.newMetricTopic);
        KStream<String, String> topicstream = builder.stream(Constants.newLogTopic);

//        metricstream.mapValues((key, value) -> {
//            System.out.println("metric key : " + key);
//            System.out.println("metric value : " + value);
//            return value;
//        });
//
//        topicstream.mapValues((key, value) -> {
//            System.out.println("log key : " + key);
//            System.out.println("log value : " + value);
//            return value;
//        });

        metricstream.filter((k, v) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ContainerMetrics metrics = objectMapper.readValue(v, ContainerMetrics.class);
                return metrics.getKubernetes().getPod().getName() != null
                        && metrics.getKubernetes().getPod().getCpu().getUsage().getLimit() != null
                        && metrics.getKubernetes().getPod().getMemory().getUsage().getLimit() != null;
            } catch (IOException | NullPointerException e) {
                return false;
            }

        }).join(topicstream, (a, b) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ContainerMetrics metrics = objectMapper.readValue(a, ContainerMetrics.class);
                ContainerLogs logs = objectMapper.readValue(b, ContainerLogs.class);
                System.out.println(metrics.getTimeStamp());
                MergeEntity mergeEntity = MergeEntity.from(new JoinWrapper(logs, metrics));
                return objectMapper.writeValueAsString(mergeEntity);
            } catch (IOException  e) {
                System.out.println(e.getMessage());
                return null;
            }
        }, JoinWindows.of(Duration.ofHours(1))).to(Constants.joinTopic);

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

    public static void main(String[] args) {
        KStreamApplication kStreamApplication = new KStreamApplication();
        kStreamApplication.init();
    }

}
