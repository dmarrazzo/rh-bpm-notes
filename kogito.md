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

Ref: https://github.com/kiegroup/kogito-examples/tree/stable/trusty-demonstration/kubernetes

Create a pod with all services:

```sh
podman pod create --name kogito -p 11222:11222 -p 9000:9000 -p 9092:9092 -p 9404:9404 -p 9405:9405
podman run -d --pod kogito --name infinispan -it -e USER="admin" -e PASS="password" --net=host quay.io/infinispan/server:12.1

podman run -d --pod kogito --restart=always \
-e LOG_DIR=/tmp/logs \
-e KAFKA_OPTS=-javaagent:/opt/kafka/libs/jmx_prometheus_javaagent-0.14.0.redhat-00002.jar=9404:/opt/kafka/custom-config/zookeeper-prometheus-config.yaml \
amqstreams:1.6.0 \
sh -c "bin/zookeeper-server-start.sh config/zookeeper.properties"

podman run -d --pod kogito --restart=always \
-e LOG_DIR=/tmp/logs \
-e KAFKA_OPTS=-javaagent:/opt/kafka/libs/jmx_prometheus_javaagent-0.14.0.redhat-00002.jar=9405:/opt/kafka/custom-config/kafka-prometheus-config.yaml \
amqstreams:1.6.0 \
sh -c "bin/kafka-server-start.sh config/server.properties --override listeners=PLAINTEXT://localhost:9092 --override advertised.listeners=PLAINTEXT://localhost:9092 --override zookeeper.connect=localhost:2181"

podman run -d --pod kogito --restart=always \
    -e KAFKA_BROKERCONNECT=localhost:9092 \
    -e JVM_OPTS="-Xms32M -Xmx128M" \
    -e SERVER_SERVLET_CONTEXTPATH="/" \
    obsidiandynamics/kafdrop:3.27.0
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

