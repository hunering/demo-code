#/bin/sh
echo "https_proxy is:${https_proxy}"
rm -rf ./yaml-cache
mkdir ./yaml-cache
cd ./yaml-cache
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/namespace.yaml
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/default-backend.yaml
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/configmap.yaml
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/tcp-services-configmap.yaml
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/udp-services-configmap.yaml

wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/rbac.yaml
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/with-rbac.yaml

kubectl delete -f ./with-rbac.yaml
kubectl delete -f ./rbac.yaml
kubectl delete -f ./udp-services-configmap.yaml
kubectl delete -f ./tcp-services-configmap.yaml
kubectl delete -f ./configmap.yaml
kubectl delete -f ./default-backend.yaml
kubectl delete -f ./namespace.yaml

cd ..
