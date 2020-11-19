# first find the zookeeper service ip by kubectl get services -A
socat tcp4-listen:2181,reuseaddr,fork tcp:192.168.145.62:2181