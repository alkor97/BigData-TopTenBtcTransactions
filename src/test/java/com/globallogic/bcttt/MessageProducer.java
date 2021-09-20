package com.globallogic.bcttt;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

public class MessageProducer implements AutoCloseable {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public MessageProducer(Properties config, String topic) {
        this.producer = new KafkaProducer<>(config);
        this.topic = topic;
    }

    @Override
    public void close() {
        producer.close();
    }

    public Future<RecordMetadata> send(String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        return producer.send(record);
    }
}
