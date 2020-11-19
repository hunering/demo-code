# first find the kubernetes-dashboard service ip by kubectl get services -A
socat tcp4-listen:8643,reuseaddr,fork tcp:192.168.145.188:443