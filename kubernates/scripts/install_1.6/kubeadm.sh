

http_proxy=http://child-prc.intel.com:913
https_proxy=http://child-prc.intel.com:913
export http_proxy
export https_proxy
export no_proxy="localhost,127.0.0.0/8,192.168.1.134,192.168.0.0/16"

kubeadm init --apiserver-advertise-address=192.168.1.134 --pod-network-cidr=192.168.32.0/19 --service-cidr=192.168.64.0/18

kubeadm init --apiserver-advertise-address=192.168.1.134 --pod-network-cidr=192.168.32.0/19 --service-cidr=192.168.64.0/18 --skip-preflight-checks

kubectl taint nodes --all node-role.kubernetes.io/master-



To start using your cluster, you need to run (as a regular user):

  sudo cp /etc/kubernetes/admin.conf $HOME/
  sudo chown $(id -u):$(id -g) $HOME/admin.conf
  export KUBECONFIG=$HOME/admin.conf

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  http://kubernetes.io/docs/admin/addons/

You can now join any number of machines by running the following on each node
as root:

  kubeadm join --token e05f70.8145d8025a6f6ff2 192.168.1.134:6443
	
	
	
kubeadm reset

systemctl daemon-reload
systemctl restart kubelet.service

kubeadm init


kubectl describe pod kube-flannel-ds-xgj12 --namespace kube-system
kubectl logs kube-flannel-ds-xgj12/kube-flannel --namespace kube-system