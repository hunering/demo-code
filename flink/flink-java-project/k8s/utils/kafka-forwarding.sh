# first find the kafka service ip by kubectl get services -A
socat tcp4-listen:6443,reuseaddr,fork tcp:192.168.150.230:9092