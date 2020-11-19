it is not easy to export the kafka port outside of the k8s.
you could use the kafka client pod to test the kafka installation.

scripts:
1. exec to the kafka client pod:
    kubectl exec -it kafka-client bash
2. kafka command:
./kafka-topics.sh --create --zookeeper zookeeper-0.zookeeper-headless.default.svc.cluster.local:2181 --replication-factor 1 --partitions 1 --topic test
./kafka-topics.sh --list --zookeeper zookeeper-0.zookeeper-headless.default.svc.cluster.local:2181
./kafka-console-producer.sh --broker-list kafka-0.kafka-headless.default.svc.cluster.local:9092 --topic test
./kafka-console-consumer.sh --bootstrap-server kafka-0.kafka-headless.default.svc.cluster.local:9092 --topic test --from-beginning

3.outside the k8s, like on the laptop, we could connect to zk, and create topics, however, we could not
  produce or consume messages