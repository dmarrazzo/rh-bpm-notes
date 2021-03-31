Quarkus
================================================

Create project
------------------------------------------------

```sh
mvn io.quarkus:quarkus-maven-plugin:1.12.2.Final:create \
    -DprojectGroupId=com.redhat.example \
    -DprojectArtifactId=service \
    -DprojectVersion=1.0.0-SNAPSHOT \
    -DclassName="com.redhat.example.MyResource"
```

Run in dev
------------------------------------------------

Run in dev mode:

	mvn compile quarkus:dev

Run with staging config: 

	mvn -Dquarkus.profile=staging compile quarkus:dev

In order to debug a problem happening at the startup you can start quarkus in suspend mode:

	mvn quarkus:dev -Dsuspend=true

Blogs
------------------------------------------------

[Quarkus: Modernize “helloworld” JBoss EAP quickstart, Part 1](https://developers.redhat.com/blog/2019/11/07/quarkus-modernize-helloworld-jboss-eap-quickstart-part-1/)
[Quarkus: Modernize “helloworld” JBoss EAP quickstart, Part 2](https://developers.redhat.com/blog/2019/11/08/quarkus-modernize-helloworld-jboss-eap-quickstart-part-2/)

Extensions
------------------------------------------------

To list available extensions:

	mvn quarkus:list-extensions

To add extensions during project creation:

mvn io.quarkus:quarkus-maven-plugin:1.3.2.Final:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=rest-json-quickstart \
    -DclassName="org.acme.rest.json.FruitResource" \
    -Dpath="/fruits" \
    -Dextensions="resteasy-jsonb"

To add extensions after project has been created:

	mvn quarkus:add-extension -Dextensions="quarkus-jdbc-mysql"


Common configurations
------------------------------------------------

- http port listener:

  ```
  quarkus.http.port=8090
  ```

- only in dev profile:

  ```
  %dev.quarkus.http.port=8090
  ```

OpenAPI
------------------------------------------------

- Add extension:

      mvn quarkus:add-extension -Dextensions="quarkus-smallrye-openapi"

- Properties:

      quarkus.swagger-ui.always-include=true


OpenShift
------------------------------------------------

extension  -Dextensions="openshift"

    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-openshift</artifactId>
    </dependency>

build:

	mvn clean package -Dquarkus.container-image.build=true

Resources use OpenShift’s DeploymentConfig:

- Configured to automatically trigger redeployment when detecting ImageStream change

[https://quarkus.io/guides/deploying-to-kubernetes#openshift]()


1. Uberjar

   	mvn clean package -Dquarkus.package.type=uber-jar

2. Create the build

	oc new-build registry.access.redhat.com/openjdk/openjdk-11-rhel7:1.1 --binary --name=people -l app=people

3. Start the build

	oc start-build people --from-file target/*-runner.jar --follow

4. Deploy

	oc new-app people && oc expose svc/people

   Check the rollout

	oc rollout status -w deployment/people

### Test the application

- Get the route

	PEOPLE_ROUTE_URL=$(oc get route people -o=template --template='{{.spec.host}}')

- Run

	curl http://${PEOPLE_ROUTE_URL}/hello

### Health check config

	oc set probe deployment/people --readiness --initial-delay-seconds=30 --get-url=http://:8080/health/ready

	oc set probe deployment/people --liveness --initial-delay-seconds=30 --get-url=http://:8080/health/live
