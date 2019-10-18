Installing HACEP Sample in OpenShift v4
==========================================

Enable AMQ Stream
------------------------------------------

This activity is the only one that requires the cluster administration privileges (`cluster-admin`).
To install the AMQ Streams Operator (v1.2):

- Open **Operators > OperatorHub**
- Select **AMQ Streams**
- Left all the default options and click **install** 

Prepare the Kafka infrastructure
------------------------------------------

The following steps can be performed as normal user (developer)

1. Create `hacep` project or choose another name.

    ```sh
    oc new-project hacep
    ```

2. Choose your project or create a new one then create the.

    - The following command create the Kafka cluster assuming `hacep` as destination project name. 

      ```sh
      cat << EOF | oc apply -f -
      apiVersion: kafka.strimzi.io/v1beta1
      kind: Kafka
      metadata:
        name: my-cluster
        namespace: hacep
      spec:
        kafka:
          version: 2.2.1
          replicas: 3
          listeners:
            external:
              type: route
            plain: {}
          config:
            offsets.topic.replication.factor: 3
            transaction.state.log.replication.factor: 3
            transaction.state.log.min.isr: 2
            log.message.format.version: '2.2'
          storage:
            type: ephemeral
        zookeeper:
          replicas: 3
          storage:
            type: ephemeral
        entityOperator:
          topicOperator: {}
          userOperator: {}
      EOF
      ```

3. Add the Kafka topics:

    ```sh
    cd kafka-topics
    oc apply -f control.yaml 
    oc apply -f events.yaml 
    oc apply -f kiesessioninfos.yaml 
    oc apply -f snapshot.yaml
    cd ..
    ```

    - otherwise in one line: `ls kafka-topics/*yaml |xargs -l1 oc apply -f `


Deploy the Rule Engine
------------------------------------------

1. Build all the projects business logic

    ```sh
    mvn clean install -DskipTests
    ```

2. Create the Rule Engine image packaged as Spring Boot application

    - Switch to `springboot` folder
    - Create the binary image

      ```sh
      oc new-build --binary --strategy=docker --name openshift-kie-springboot
      oc start-build openshift-kie-springboot --from-dir=. --follow
      ```

3. Deploy the Rule Engine

    - The following steps are performed from the `springboot` folder
    - Create a service account with privileges to manage the ConfigMaps. A ConfigMap is used for the leader election.

      ```sh
      oc create -f kubernetes/service-account.yaml
      oc create -f kubernetes/role.yaml
      oc create -f kubernetes/role-binding.yaml
      ```
    
    - Get the image name

      ```sh
      oc get is/openshift-kie-springboot -o template --template='{{range .status.tags}}{{range .items}}{{.dockerImageReference}}{{end}}{{end}}'
      ```

    - Open `kubernetes/deployment.yaml` and replace existing image URL with the result of the previous command trimming the tail after `@` symbol then add `:latest`. 
      E.g. `image: image-registry.openshift-image-registry.svc:5000/hacep/openshift-kie-springboot:latest`

    - Deploy the image

      ```sh
      oc apply -f kubernetes/deployment.yaml
      ```

Run the client sample (events injector)
------------------------------------------

1. Configure the SSL communication

    - enter in the client folder

      ```sh
      cd sample-hacep-project/sample-hacep-project-client
      ```

    - create the key store
    
      ```sh
      rm src/main/resources/keystore.jks
      keytool -genkeypair -keyalg RSA -keystore src/main/resources/keystore.jks
      ```

    - extract the kafka cluster certification authority 

      ```sh
      oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > src/main/resources/ca.crt
      ```

    - add the kafka CA to the client key store (in the following step we assume `password` as key store password, otherwise change it accordingly)

      ```sh
      keytool -import -trustcacerts -alias root -file src/main/resources/ca.crt -keystore src/main/resources/keystore.jks -storepass password -noprompt
      ```

2. Configure the client

    - get the kafka bootstrap endpoint with the following command

      ```sh
      oc get route/my-cluster-kafka-bootstrap
      ```

    - edit `src/main/resources/configuration.properties` to update the kafka bootstrap server host (adding `:443` at the end) and the other details.

      ```
      ssl.keystore.location=src/main/resources/keystore.jks
      ssl.truststore.location=src/main/resources/keystore.jks
      ssl.keystore.password=password
      ssl.truststore.password=password
      bootstrap.servers=my-cluster-kafka-bootstrap-hacep.apps-crc.testing:443
      security.protocol=SSL
      ```

3. Execute the client

    ```sh
    mvn exec:java -Dexec.mainClass="org.kie.hacep.sample.client.ClientProducerDemo"
    ```

Check the results on the Rule Engine
------------------------------------------

1. Identify the Rule Engine leader 

    ```sh
    oc get cm/default-leaders -o template --template='{{range $k,$v := .data}}{{if eq $k "leader.pod.null"}}{{printf "%s\n" $v}}{{end}}{{end}}'
    ```

2. Inspect the log of the leader pod. E.g. `oc logs -f openshift-kie-springboot-c8b9c6545-2p8x4`

3. Check the presence of this information: `Price for RHT is <...> `



Issues
------------------------------------------

- The following warning on client side could be caused by an erroneous server host configuration, make sure that hostnames are resolved and the correct port is defined (443).

  ```
  WARN  o.a.kafka.clients.NetworkClient - [Consumer clientId=consumer-1, groupId=drools] Connection to node -1 (my-cluster-kafka-bootstrap-hacep.apps-crc.testing/192.168.130.11:9094) could not be established. Broker may not be available.
  ```

