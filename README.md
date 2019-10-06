# RestAkkaLogger2

0. start docker VM: ```docker-machine start default```

1. sh build_and_deploy.sh

2. From terminal, run ```docker-machine ip``` (activate machine if its turned off).

3. Send Restful command to [DOCKER VM IP] port 8080. 
IG:
```
curl -X PUT 'http://[DOCKER VM IP]:8080/log?level=debug&content=AAAAAAAAAF'
```

4. Level must be one of {trace, debug, info, warn, error, critical}. Otherwise an error message will be returned.





# Scenarios:
1) After creating container, cannot curl (Connection refused):
- turn off VM ('default')
- ```docker-machine start default```
- ```docker-machine env```
- ```eval $(docker-machine env)```
- try to create conainer again: ```docker run -t -p 8080:8080 --name akka-server-container akka-server-image```
