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

Kogito provides visual editors for Visual Studio Code:

[Kogito Bundle](https://marketplace.visualstudio.com/items?itemName=kie-group.vscode-extension-kogito-bundle)

Kogito runtime
---------------------------------------------------------

Archetypes:

- kogito-quarkus-archetype
- kogito-springboot-archetype
- kogito-quarkus-dm-archetype
- kogito-springboot-dm-archetype

Create:

```
mvn archetype:generate \
    -DartifactId=sample-kogito \
    -DgroupId=org.acme -Dversion=1.0-SNAPSHOT \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-quarkus-archetype \
    -DarchetypeVersion=1.10.0.Final
```

Kogito Supporting Services 
---------------------------------------------------------

**Podman** is much easier for Linux users than Docker, moreover it does not require root access.  
For such reason, I collected those script that help Kogito users to set up a local environment.
They could be useful also for docker users who prefer to have finer control.

The following instruction leverages assets from [kogito-example](https://github.com/kiegroup/kogito-examples) repository.


> **Important Network Note**: Whenever a container has to access to other containers (in different pods), it needs the *ip address* of the hosting machine. A pratical trick is to use a *stable ip* who does not change over the time (not DHCP provided). It's likely that you already have such *ip address* in your system, in case you can use **podman network command** to create it. Once you have found your stable IP, you can refer to it using a local name (*thehost*) and binding it in `/etc/hosts` e.g.:
>
> ```
> 192.168.130.1 thehost
> ```
> 
> The remaining doc assumes you have defined `thehost` in your naming service.


### PostgresSQL

```sh
podman pod create --name kogito-pg -p 5432:5432 -p 8055:80

podman run --name kogito-pg-server \
       --pod kogito-pg \
       -d \
       -e POSTGRES_USER=kogito-user \
       -e POSTGRES_PASSWORD=kogito-pass \
       -e POSTGRES_DB=kogito \
       postgres:13

podman run --name kogito-pgadmin \
       --pod kogito-pg \
       -d \
       -e PGADMIN_DEFAULT_EMAIL=user@user.org \
       -e PGADMIN_DEFAULT_PASSWORD=pass \
       dpage/pgadmin4:5.0
```

### Infinispan persistence

As an alternative to PostgresSQL persistence:

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

> In case of problems with **SELinux** you may try the following solution:
>
>     chcon -Rt svirt_sandbox_file_t sql

### Kafka messaging

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

### Data Index (PostgreSQL based)

```sh
podman pod create --name kogito-data-index-pg -p 8180:8080

podman run --name kogito-data-index-pg-server \
       --pod kogito-data-index-pg \
       -d \
       -e KAFKA_BOOTSTRAP_SERVERS=thehost:9092 \
       -e QUARKUS_DATASOURCE_DB-KIND=postgresql \
       -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://thehost:5432/kogito \
       -e QUARKUS_DATASOURCE_USERNAME=kogito-user \
       -e QUARKUS_DATASOURCE_PASSWORD=kogito-pass \
       -e QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION=update \
       quay.io/kiegroup/kogito-data-index-postgresql:latest
```

It's possible to manually initialize the DB Schema, removing the `QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION` and using the following SQL script: https://github.com/kiegroup/kogito-apps/blob/main/data-index/data-index-storage/data-index-storage-postgresql/src/main/resources/create.sql

### Data Index (Infinispan based)

In case you are using the Infinispan as persistence layer, it requires data type definitions in protobuf 

```sh
mkdir -p ./target/protobuf
find .. -name "persistence" -print0 | xargs -0 -I \1 find \1 -name "*.proto" -exec cp {} target/protobuf/ \;

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

### Management Console 

It requires SVG images of the BPMN diagrams

```sh
mkdir svg
find .. -iname "*.svg" -exec cp {} svg/ \;

podman pod create --name kogito-management-console -p 8280:8080

podman rm -f kogito-management-console-server

podman run --name kogito-management-console-server \
       --pod kogito-management-console \
       -d \
       -e KOGITO_DATAINDEX_HTTP_URL=http://thehost:8180 \
       -e KOGITO_MANAGEMENT_CONSOLE_PROPS=-Dkogito.svg.folder.path=/home/kogito/data/svg \
       -v ./svg/:/home/kogito/data/svg/ \
       quay.io/kiegroup/kogito-management-console:latest
```

### Task console

```sh  
podman pod create --name kogito-task-console -p 8380:8080

podman run --name kogito-task-console-server \
       --pod kogito-task-console \
       -d \
       -e KOGITO_TASK_CONSOLE_PROPS=-Dkogito.test.user-system.enabled=true \
       quay.io/kiegroup/kogito-task-console:latest
```    

### Jobs Service (PostgreSQL)

> **Note** Due to a current limitation (v1.9.1) Jobs Service expects to find an empty DB.  

```sh
podman pod create --name kogito-jobs-service -p 8085:8080

podman run --name kogito-jobs-service-server \
       --pod kogito-jobs-service \
       -d \
       -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://thehost:5432/kogito \
       -e QUARKUS_DATASOURCE_REACTIVE_URL=postgresql://thehost:5432/kogito \
       -e QUARKUS_DATASOURCE_USERNAME=kogito-user  \
       -e QUARKUS_DATASOURCE_PASSWORD=kogito-pass \
       quay.io/kiegroup/kogito-jobs-service-postgresql:latest
```


OpenShift Deployment
---------------------------------------------------------

Tested integrations: 

https://github.com/kiegroup/kogito-operator#kogito-operator-tested-integrations

Infinispan Operator 2.1.x

```yaml
apiVersion: infinispan.org/v1
kind: Infinispan
metadata:
  name: example-infinispan
  namespace: kogito-exp
spec:
  replicas: 1
```

Protobuf configmap:

  oc create configmap data-index-protobuf-def --from-file=travels.proto --from-file=visaApplications.proto 
  oc label configmap data-index-protobuf-def kogito-protobuf=true

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

