package kafka;

import org.apache.kafka.clients.producer.*;

import java.io.Serializable;
import java.util.Properties;

public class MyKafkaProducer implements Serializable {
    private Producer<String, String> producer;
    private String topic;
    private String bootstrapServer;

    public void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public void send(String key, String message) {
        producer.send(new ProducerRecord<>(topic, key, message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                System.out.println("record : " + recordMetadata.toString() + " completed");
            }
        });
    }

    public static class Builder {
        private String topic;
        private String bootstrapServer;

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setBootstrapServer(String bootstrapServer) {
            this.bootstrapServer = bootstrapServer;
            return this;
        }

        public  MyKafkaProducer build() {
            MyKafkaProducer myKafkaProducer = new MyKafkaProducer();
            myKafkaProducer.setBootstrapServer(bootstrapServer);
            myKafkaProducer.setTopic(topic);
            myKafkaProducer.init();
            return myKafkaProducer;
        }
    }
}
