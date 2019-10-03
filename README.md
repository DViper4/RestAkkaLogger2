# RestAkkaLogger2

1. sh build_and_deploy.sh

2. From terminal, run ```docker-machine ip``` (activate machine if its turned off).

3. Send Restful command to [DOCKER VM IP] port 8080. 
IG:
```
curl -X PUT 'http://[DOCKER VM IP]:8080/log?level=debug&content=AAAAAAAAAF'
```

4. Level must be one of {trace, debug, info, warn, error, critical}. Otherwise an error message will be returned.
