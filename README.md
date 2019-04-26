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
make build tag-latest push -e VERSION=0.1.0  --always-make
```

## Release process

```
DOCSTAMPR_VERSION=0.2.0
git tag -a $DOCSTAMPR_VERSION -m "${VERSION_SSM} version"
git checkout $DOCSTAMPR_VERSION
make build tag-latest push -e VERSION=$DOCSTAMPR_VERSION  --always-make
git push origin $DOCSTAMPR_VERSION
```