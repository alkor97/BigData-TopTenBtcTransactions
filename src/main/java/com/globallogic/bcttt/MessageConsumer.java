package com.globallogic.bcttt;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

public class MessageConsumer implements AutoCloseable, Runnable {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    public MessageConsumer(Properties config, String topic) {
        this.consumer = new KafkaConsumer<>(config);
        this.topic = topic;
    }

    @Override
    public void close() throws InterruptedException {
        try {
            running.set(false);
            countDownLatch.await();
        } finally {
            consumer.close();
        }
    }

    @Override
    public void run() {
        System.out.println("subscribed to topic " + topic + "...");
        consumer.subscribe(Collections.singletonList(topic));
        while (running.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            processRecords(records);
            consumer.commitSync();
        }
        countDownLatch.countDown();
    }

    protected boolean processRecords(ConsumerRecords<String, String> records) {
        long numberOfChanges =  StreamSupport.stream(records.records(topic).spliterator(), false)
                .map(this::processRecord)
                .filter(v -> v)
                .count();
        return numberOfChanges > 0;
    }

    protected boolean processRecord(ConsumerRecord<String, String> record) {
        return false;
    }

    public void cancel() {
        running.set(false);
    }
}
