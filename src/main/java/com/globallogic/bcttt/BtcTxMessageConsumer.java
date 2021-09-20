package com.globallogic.bcttt;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Properties;

public class BtcTxMessageConsumer extends MessageConsumer {

    private final MessageParser parser = new MessageParser();
    private final TopPriceTransactions topPriceTxs;

    public BtcTxMessageConsumer(Properties config, String topic, int limit) {
        super(config, topic);
        this.topPriceTxs = new TopPriceTransactions(limit);
    }

    @Override
    protected boolean processRecords(ConsumerRecords<String, String> records) {
        if (super.processRecords(records)) {
            System.out.println(topPriceTxs);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processRecord(ConsumerRecord<String, String> record) {
        String message = record.value();
        return parser.parse(message)
                .flatMap(Message::getPrice)
                .map(price -> topPriceTxs.add(price, message))
                .orElse(false);
    }

    TopPriceTransactions getState() {
        return topPriceTxs;
    }
}
