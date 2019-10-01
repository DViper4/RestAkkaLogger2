# RestAkkaLogger2

1. Send Restful command to localhost port 8080. IG:
```
curl -X PUT 'http://localhost:8080/log?level=debug&content=AAAAAAAAAF'
```
or use your browser instead.

# running docker
```
mvn clean package  
docker build -f Dockerfile -t akka-rest-logger .  
docker run -d  -p 8080:8080 akka-rest-logger 
ps -a 	# to ensure container is running
```
