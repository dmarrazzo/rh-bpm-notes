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

1. Create kafka cluster

- create the following yaml and name it `mycluster.yaml`

```yaml
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
      tls: {}
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
```

- create the cluster with the following command: `oc apply -f mycluster.yaml`

2. Add kafka topics:

```sh
cd kafka-topics
oc apply -f control.yaml 
oc apply -f events.yaml 
oc apply -f kiesessioninfos.yaml 
oc apply -f snapshot.yaml
cd ..
```

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

    - Open the deployment yaml and replace existing image URL with the result of the previous command trimming the tail after `@` symbol then add `:latest`. 
      E.g. `image: image-registry.openshift-image-registry.svc:5000/hacep/openshift-kie-springboot:latest`


Run the client sample (events injector)
------------------------------------------

WIP

Issues
------------------------------------------

Problem to load properties:
https://github.com/kiegroup/openshift-drools-hacep/blob/master/sample-hacep-project/sample-hacep-project-client/src/main/java/org/kie/hacep/sample/client/ClientProducerDemo.java#L53


