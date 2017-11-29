APISERVER=$(kubectl config view | grep server | cut -f 2- -d ":" | tr -d " ")
TOKEN=$(kubectl describe secret $(kubectl get secrets --namespace=default | grep apiserver-access | cut -f1 -d ' ') --namespace=default | grep -E '^token' | cut -f2 -d':' | tr -d '\t ')
curl $APISERVER/api/v1/nodes --header "Authorization: Bearer $TOKEN" --insecure
