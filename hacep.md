Installing HACEP Sample in OpenShift v4
==========================================

Prepare the Cluster
------------------------------------------

As Cluster administrator, you have to install the AMQ Streams operator (v1.2)

- Open **Operators > OperatorHub**
- Select **AMQ Streams**
- Click **install**

Install the Decision Server
------------------------------------------

The following step can be performed as normal user (developer)

1. Choose your project or create a new one then create the **kafka cluster** in that project.

    - The following command create the kafka cluster assuming `hacep` as destination project name. 

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

2. Add kafka topics:

    ```sh
    cd kafka-topics
    oc apply -f control.yaml 
    oc apply -f events.yaml 
    oc apply -f kiesessioninfos.yaml 
    oc apply -f snapshot.yaml
    cd ..
    ```

    - otherwise in one line: `ls kafka-topics/*yaml |xargs -l1 oc apply -f `

3. Build the pods (skip?)

    ```sh
    mvn clean install -DskipTests
    ```

4. Deploy the Decision Server in Spring Boot

    - Enter in the springboot directory
    - In order grant the access to the ConfigMaps run the following OCP definitions:

      ```sh
      oc create -f kubernetes/service-account.yaml
      oc create -f kubernetes/role.yaml
      oc create -f kubernetes/role-binding.yaml
      ```

    - Create the image

      ```sh
      oc new-build --binary --strategy=docker --name openshift-kie-springboot
      oc start-build openshift-kie-springboot --from-dir=. --follow
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

WIP

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

Check the results on the Decision Server
------------------------------------------

1. Identify the Decision Server leader 

    ```sh
    oc get cm/default-leaders -o template --template='{{range $k,$v := .data}}{{if eq $k "leader.pod.null"}}{{printf "%s\n" $v}}{{end}}{{end}}'
    ```

2. Inspect the log of the leader pod. E.g. `oc logs -f openshift-kie-springboot-c8b9c6545-2p8x4`

3. Check the presence of this information: `Price for RHT is 81.0 `



Issues
------------------------------------------

Problem to load properties:
https://github.com/kiegroup/openshift-drools-hacep/blob/master/sample-hacep-project/sample-hacep-project-client/src/main/java/org/kie/hacep/sample/client/ClientProducerDemo.java#L53


