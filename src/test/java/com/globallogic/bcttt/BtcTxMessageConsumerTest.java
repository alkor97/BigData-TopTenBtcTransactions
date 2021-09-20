package com.globallogic.bcttt;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BtcTxMessageConsumerTest {

    private static class MyEmbeddedKafkaCluster extends EmbeddedKafkaCluster {
        public MyEmbeddedKafkaCluster(int numBrokers) {
            super(numBrokers);
        }

        public void before() throws Throwable {
            super.before();
        }

        public void after() {
            super.after();
        }
    }

    private final MyEmbeddedKafkaCluster kafkaCluster = new MyEmbeddedKafkaCluster(1);

    @Before
    public void before() throws Throwable {
        System.out.println("starting Kafka cluster...");
        kafkaCluster.before();
    }

    @After
    public void after() {
        System.out.println("stopping Kafka cluster...");
        kafkaCluster.after();
    }

    @Test
    public void test() throws InterruptedException {
        final String topic = "my-private-test-topic";
        kafkaCluster.createTopic(topic);

        final Properties common = new Properties();
        common.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster.bootstrapServers());
        common.put(CommonClientConfigs.GROUP_ID_CONFIG, "my-private-test-group");

        final Properties producerConfig = new Properties();
        producerConfig.putAll(common);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        final Properties consumerConfig = new Properties();
        consumerConfig.putAll(common);
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put("enable.auto.commit", "false");

        try (MessageProducer producer = new MessageProducer(producerConfig, topic)) {
            try (Consumer consumer = new Consumer(consumerConfig, topic, 2, 3)) {
                Thread thread = new Thread(consumer);
                thread.start();

                IntStream.rangeClosed(1, 3)
                        .mapToObj(v -> String.format("src/test/resources/message%d.json", v))
                        .map(Paths::get)
                        .map(this::readFromPath)
                        .forEach(content -> producer.send(null, content));

                thread.join(30_000);

                Iterator<Map.Entry<Double, String>> iterator = consumer.getState().iterator();

                assertTrue(iterator.hasNext());
                Map.Entry<Double, String> pair = iterator.next();
                assertEquals(19000.67, pair.getKey(), 0.01);

                assertTrue(iterator.hasNext());
                pair = iterator.next();
                assertEquals(18058.68, pair.getKey(), 0.01);

                assertFalse(iterator.hasNext());
            }
        }
    }

    private String readFromPath(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            return null;
        }
    }

    private static final class Consumer extends BtcTxMessageConsumer {

        private int stopAfterMessages;

        public Consumer(Properties config, String topic, int limit, int stopAfterMessages) {
            super(config, topic, limit);
            this.stopAfterMessages = stopAfterMessages;
        }

        @Override
        protected boolean processRecord(ConsumerRecord<String, String> record) {
            try {
                return super.processRecord(record);
            } finally {
                if (0 == --stopAfterMessages) {
                    super.cancel();
                }
            }
        }
    }
}
