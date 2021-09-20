
Bitcoin-to-USD transactions consumer.

How to build?
    Requirements:
         - JDK 8+
         - Maven 3+
    `mvn clean install -DskipTests` generates file `BitcoinTopTenTransactions-1.0-SNAPSHOT-jar-with-dependencies.jar`

How to run?
    `java -jar BitcoinTopTenTransactions-1.0-SNAPSHOT-jar-with-dependencies.jar`
    First run will generate `consumer.properties` file, which needs to be updated in terms of `bootstrap.servers` parameter.
    During first run also Kafka topic creation and deletion scripts are created.
    Next runs will use content of `consumer.properties` to proceed.
