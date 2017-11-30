kubectl delete -f ./pod-reader-role-bind.yaml
kubectl delete -f ./pod-reader-role.yaml
kubectl delete -f ./pod-reader-sa.yaml

kubectl create -f ./pod-reader-sa.yaml
kubectl create -f ./pod-reader-role.yaml
kubectl create -f ./pod-reader-role-bind.yaml

APISERVER=$(kubectl config view | grep server | cut -f 2- -d ":" | tr -d " ")
TOKEN=$(kubectl describe secret $(kubectl get secrets --namespace=default | grep pod-reader | cut -f1 -d ' ') --namespace=default | grep -E '^token' | cut -f2 -d':' | tr -d '\t ')
curl $APISERVER/api/v1/namespaces/default/pods --header "Authorization: Bearer $TOKEN" --insecure

