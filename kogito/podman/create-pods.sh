#!/bin/sh

set HOSTIP (ip route get 1.2.3.4 | awk '{print $7}' | xargs)

podman pod create --name kogito-infra \
       -p 11222:11222 \
       -p 2181:2181 \
       -p 9092:9092 \
       -p 29092:29092 \
       -p 9090:9090 \
       -p 3000:3000

# infinispan
podman run -d --pod kogito-infra --name infinispan \
       -v ./infinispan/infinispan.xml:/opt/infinispan/server/conf/infinispan-demo.xml:Z \
       quay.io/infinispan/server:11.0.4.Final \
       /opt/infinispan/bin/server.sh -c infinispan-demo.xml

# kafka zookeeper
podman run -d --pod kogito-infra --name zookeeper --restart=always \
       -e LOG_DIR=/tmp/logs \
       docker.io/strimzi/kafka:0.20.1-kafka-2.6.0 \
       sh -c "bin/zookeeper-server-start.sh config/zookeeper.properties"

# kafka server
podman run -d --pod kogito-infra --name kafka --restart=always \
           -e KAFKA_BROKER_ID=0 \
           -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
           -e KAFKA_LISTENERS=INTERNAL://localhost:29092,EXTERNAL://localhost:9092 \
           -e KAFKA_ADVERTISED_LISTENERS=INTERNAL://localhost:29092,EXTERNAL://$HOSTIP:9092 \
           -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT \
           -e KAFKA_INTER_BROKER_LISTENER_NAME=INTERNAL \
           -e KAFKA_AUTO_CREATE_TOPICS_ENABLE="true" \
           -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
           -e LOG_DIR=/tmp/logs \
           docker.io/strimzi/kafka:0.20.1-kafka-2.6.0 \
           sh -c "bin/kafka-server-start.sh config/server.properties --override inter.broker.listener.name=\${KAFKA_INTER_BROKER_LISTENER_NAME} --override listener.security.protocol.map=\${KAFKA_LISTENER_SECURITY_PROTOCOL_MAP} --override listeners=\${KAFKA_LISTENERS} --override advertised.listeners=\${KAFKA_ADVERTISED_LISTENERS} --override zookeeper.connect=\${KAFKA_ZOOKEEPER_CONNECT}"

# kafka drop
# podman run -d --pod kogito-infra --restart=always \
#     -e KAFKA_BROKERCONNECT=localhost:9092 \
#     -e JVM_OPTS="-Xms32M -Xmx128M" \
#     -e SERVER_SERVLET_CONTEXTPATH="/" \
#     obsidiandynamics/kafdrop:3.27.0

# prometheus
podman run -d --pod kogito-infra --name prometheus --restart=always \
       -v ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:Z \
       docker.io/prom/prometheus:v2.8.0 \
       --config.file=/etc/prometheus/prometheus.yml

# grafana
podman run -d --pod kogito-infra --name grafana --restart=always \
       -v ./grafana/provisioning/:/etc/grafana/provisioning/:Z \
       -e PROMETHEUS_URL=http://localhost:9090 \
       docker.io/grafana/grafana:6.6.1

# kogito supporting services

set KOGITO_VERSION 1.6.0

# data-index pod
podman pod create --name kogito-data-index \
       -p 8080:8180

# data-index
       #-v ./target/protobuf:/home/kogito/data/protobufs/ \
       #-e KOGITO_DATA_INDEX_PROPS=-Dkogito.protobuf.folder=/home/kogito/data/protobufs/ \
podman run -d --pod kogito-data-index --name data-index --restart=always \
       -e QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=$HOSTIP:11222 \
       -e KAFKA_BOOTSTRAP_SERVERS=$HOSTIP:9092 \
       quay.io/kiegroup/kogito-data-index-infinispan:$KOGITO_VERSION

# the following works
podman run -d --pod kogito-infra --name data-index --restart=always \
       -e QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=localhost:11222 \
       -e KAFKA_BOOTSTRAP_SERVERS=localhost:29092 \
       quay.io/kiegroup/kogito-data-index-infinispan:$KOGITO_VERSION

# management-console pod
podman pod create --name kogito-management-console \
       -p 8080:8280

# management-console
       #-v ./svg/:/home/kogito/data/svg/ \
podman run -d --pod kogito-management-console --name management-console --restart=always --network=host \
       -e KOGITO_DATAINDEX_HTTP_URL=http://localhost:8180 \
       -e KOGITO_MANAGEMENT_CONSOLE_PROPS=-Dkogito.svg.folder.path=/home/kogito/data/svg \
       quay.io/kiegroup/kogito-management-console:${KOGITO_VERSION} 
