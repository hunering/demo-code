# first find one of the kafka headless server ip(kafka-0.kafka-headless.default.svc.cluster.local)
socat tcp4-listen:9092,reuseaddr,fork tcp:192.168.150.230:9092