kubectl delete daemonsets,replicasets,services,deployments,pods,rc --all
kubectl apply -f akka-server.yaml
kubectl expose deployment log-server --type=LoadBalancer --name=log-server
minikube service  log-server
~
~
