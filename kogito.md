Kogito
=========================================================

Learning resources
---------------------------------------------------------

- https://github.com/KIE-Learning

Embedded
---------------------------------------------------------

https://github.com/kiegroup/kogito-runtimes/tree/master/integration-tests/integration-tests-quarkus-norest

Kogito tooling
---------------------------------------------------------

Installation from: [github release](https://github.com/kiegroup/kogito-tooling/releases)

In order to enable the code:

```sh
sudo vi /usr/bin/code
```

Change this line:

```sh
ELECTRON_RUN_AS_NODE=1 "$ELECTRON" "$CLI" "--enable-proposed-api" "kiegroup.vscode-extension-pack-kogito-kie-editors" "$@"
```

### Open with text editor

![reopen](imgs/reopen.gif)

Kogito supporting services 
---------------------------------------------------------

Network trick: use a default hostname (*thehost*) to one of your host ip, e.g. `/etc/host`:

```
192.168.130.1 thehost
```

**To check** - *kogito-kafka* is a temporary workaround

### Postgress

Using **process-postgresql-persistence-quarkus**

```sh
cd $KOGITO_EXAMPLES/kogito-examples/process-postgresql-persistence-quarkus/docker-compose

podman pod create --name kogito-pg -p 5432:5432 -p 8055:80

chcon -Rt svirt_sandbox_file_t sql

podman run --name kogito-postgres \
       --pod kogito-pg \
       -d \
       -v ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql \
       -e POSTGRES_PASSWORD=pass \
       postgres:9.6

podman run --name kogito-pgadmin \
       --pod kogito-pg \
       -d \
       -e PGADMIN_DEFAULT_EMAIL=user@user.org \
       -e PGADMIN_DEFAULT_PASSWORD=pass \
       dpage/pgadmin4:5.0
```

### Travel Agency

Ref: https://github.com/kiegroup/kogito-examples/tree/stable/trusty-demonstration/kubernetes

Kogito shared services:

- **Infinispan persistence**

  ```sh
  podman pod create --name kogito-infinispan -p 11222:11222
  
  podman run --name kogito-ifs \
         --pod kogito-infinispan \
         -d \
         --tmpfs /tmp \
         -v ./infinispan/infinispan.xml:/opt/infinispan/server/conf/infinispan-demo.xml:z \
         infinispan/server:12.1.4.Final \
         /opt/infinispan/bin/server.sh -c infinispan-demo.xml
  ```

- **Kafka messaging**

  ```sh
  podman pod create --name kogito-kafka -p 9092:9092
  
  podman run --name kogito-zookeeper \
         --pod kogito-kafka \
         -d \
         -e LOG_DIR=/tmp/logs \
         --tmpfs /tmp \
         strimzi/kafka:0.20.1-kafka-2.6.0 \
         bin/zookeeper-server-start.sh config/zookeeper.properties
  
  podman run --name kogito-kafka-server \
         --pod kogito-kafka \
         -d \
         -e KAFKA_BROKER_ID=0 \
         -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
         -e KAFKA_LISTENERS=INTERNAL://localhost:29092,EXTERNAL://0.0.0.0:9092 \
         -e KAFKA_ADVERTISED_LISTENERS=INTERNAL://localhost:29092,EXTERNAL://thehost:9092 \
         -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT \
         -e KAFKA_INTER_BROKER_LISTENER_NAME=INTERNAL \
         -e KAFKA_AUTO_CREATE_TOPICS_ENABLE="true" \
         -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
         -e LOG_DIR="/tmp/logs" \
         --tmpfs /tmp \
         --requires kogito-zookeeper \
         --entrypoint bash \
         strimzi/kafka:0.20.1-kafka-2.6.0 \
         -c "bin/kafka-server-start.sh config/server.properties --override inter.broker.listener.name=\${KAFKA_INTER_BROKER_LISTENER_NAME} --override listener.security.protocol.map=\${KAFKA_LISTENER_SECURITY_PROTOCOL_MAP} --override listeners=\${KAFKA_LISTENERS} --override advertised.listeners=\${KAFKA_ADVERTISED_LISTENERS} --override zookeeper.connect=\${KAFKA_ZOOKEEPER_CONNECT}"
    ```

- **Data Index** It requires data type definitions in protobuf 

  ```sh
  mkdir -p ./target/protobuf
  find .. -name "persistence" -print0 | xargs -0 -I \1 find \1 -name "*.proto" -exec cp {} target/  protobuf/ \;
  
  podman pod create --name kogito-data-index -p 8180:8080
  
  podman run --name kogito-data-index-server \
         --pod kogito-data-index \
         -d \
         -e QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=thehost:11222 \
         -e KAFKA_BOOTSTRAP_SERVERS=thehost:9092 \
         -e KOGITO_DATA_INDEX_PROPS=-Dkogito.protobuf.folder=/home/kogito/data/protobufs/ \
         -v ./target/protobuf:/home/kogito/data/protobufs:Z \
         quay.io/kiegroup/kogito-data-index-infinispan:latest
  ```

- **Management Console** It requires SVG images of the BPMN diagrams

  ```sh
  mkdir svg
  find .. -iname "*.svg" -exec cp {} svg/ \;
  
  podman pod create --name kogito-management-console -p 8280:8080
  
  podman run --name kogito-management-console-server \
         --pod kogito-management-console \
         -d \
         -e KOGITO_DATAINDEX_HTTP_URL=http://thehost:8180 \
         -e KOGITO_MANAGEMENT_CONSOLE_PROPS=-Dkogito.svg.folder.path=/home/kogito/data/svg \
         -v ./svg/:/home/kogito/data/svg/ \
         quay.io/kiegroup/kogito-management-console:latest
  ```

OpenShift Deployment
---------------------------------------------------------

Infinispan Operator 2.0.6

```yaml
apiVersion: infinispan.org/v1
kind: Infinispan
metadata:
  name: example-infinispan
  namespace: kogito-exp
spec:
  replicas: 1
```

Kogito operator:

Infra configuration:

```yaml
apiVersion: app.kiegroup.org/v1beta1
kind: KogitoInfra
metadata:
  name: kogito-infinispan-infra
  namespace: kogito-exp
spec:
  resource:
    apiVersion: infinispan.org/v1
    kind: Infinispan
    name: example-infinispan
```

Deploy runtime from local dir:
check:
```sh
kogito deploy-service proc-infinispan . --infra kogito-infinispan-infra
```

Kogito Runtime outcome:

```yaml
apiVersion: app.kiegroup.org/v1beta1
kind: KogitoRuntime
metadata:
  name: proc-infinispan
spec:
  replicas: 1
  infra:
    - kogito-infinispan-infra
```


### Errors:

Lack of infinispan configuration (points to the wrong address 127.0.0.1)

```log
2021-05-19 14:14:44,948 INFO  [org.inf.HOTROD] (main) ISPN004021: Infinispan version: Infinispan 'Corona Extra' 11.0.8.Final
2021-05-19 14:14:45,027 ERROR [org.inf.HOTROD] (HotRod-client-async-pool-1-1) ISPN004007: Exception encountered. Retry 10 out of 10: io.netty.channel.AbstractChannel$AnnotatedConnectException: finishConnect(..) failed: Connection refused: /127.0.0.1:11222
Caused by: java.net.ConnectException: finishConnect(..) failed: Connection refused
	at io.netty.channel.unix.Errors.throwConnectException(Errors.java:124)
```

