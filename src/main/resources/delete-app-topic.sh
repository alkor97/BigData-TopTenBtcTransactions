#!/bin/bash

/usr/lib/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic btcusd-transactions
