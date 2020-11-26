1. for single kafka node, cd "single-node", run "docker-compose up -d"
2. for cluster kafka, cd "cluster", run "docker-compose up -d"
    connect to grafana: docker-compose exec grafana sh

3. to connect to kafka outside docker：
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic dcm_power
./kafka-topics.sh --list --zookeeper localhost:2181
./kafka-console-producer.sh --broker-list localhost:9093 --topic dcm_power < /home/huxiaomi/work/demo-code/flink/flink-java-project/src/test/java/com/hxm/dcm/flink/power/power_data_1.txt
./kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9093 --topic dcm_power --from-beginning

./zookeeper-shell.sh localhost:2181 ls /brokers/ids

./kafka-topics.sh --zookeeper localhost:2181 --delete --topic dcm_power

4. ref:
https://github.com/bitnami/bitnami-docker-kafka

ps:

./flink run -c com.hxm.dcm.flink.power.PowerMsgCount ~/work/demo-code/flink/flink-java-project/target/flink-java-project-0.1.jar
./flink list
./flink cancel 2906e9e87593daff30a66e14b11c9ef6
./flink run -s /home/huxiaomi/temp/savepoint/savepoint-cdc853-489b2b8e9d6e -c com.hxm.dcm.flink.power.PowerMsgCount ~/work/demo-code/flink/flink-java-project/target/flink-java-project-0.1.jar