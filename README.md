# Docstampr

 * Start test network based on bclan and with ssm
```
docker-compose -f docker-compose-it.yaml up -d
```

## Edit /etc/host

```
127.0.0.1	ca.bc-coop.bclan
127.0.0.1	peer0.bc-coop.bclan
127.0.0.1	orderer.bclan

```


## Build project

```
./gradlew build
```


Rest api use ssm with docstampr

```
make build tag-latest push -e VERSION=0.1.0
```

## Release process

```
git tag -a 0.1.0 -m "First version"
git checkout 0.1.0
```