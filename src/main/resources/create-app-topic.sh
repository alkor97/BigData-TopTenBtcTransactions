#!/bin/bash

/usr/lib/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic btcusd-transactions --replication-factor 2 --partitions 1
