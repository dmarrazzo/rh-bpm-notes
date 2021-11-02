Quarkus
================================================

Create project
------------------------------------------------

```sh
mvn io.quarkus:quarkus-maven-plugin:1.13.7.Final:create \
    -DprojectGroupId=com.redhat.example \
    -DprojectArtifactId=rest-service \
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

### Forms from Open APIs

Add the following dependency and open the URL http://<host>:<port>/openapi-ui-forms/

```xml
    <dependency>
      <groupId>io.smallrye</groupId>
      <artifactId>smallrye-open-api-ui-forms</artifactId>
      <version>2.1.3</version>
      <scope>runtime</scope>
    </dependency>
```

https://github.com/smallrye/smallrye-open-api/tree/main/ui/open-api-ui-forms

OpenShift
------------------------------------------------

Add extension  -Dextensions="openshift"

    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-openshift</artifactId>
    </dependency>

build:

	mvn clean package -Dquarkus.container-image.build=true

Resources use OpenShift’s DeploymentConfig:

- Configured to automatically trigger redeployment when detecting ImageStream change

  [https://quarkus.io/guides/deploying-to-kubernetes#openshift]()

Manual deploy:

1. Uberjar
  
   ```
   mvn clean package -Dquarkus.package.type=uber-jar
   ```

2. Create the build

	   oc new-build registry.access.redhat.com/openjdk/openjdk-11-rhel7:1.1 --binary --name=people -l app=people

3. Start the build

	   oc start-build people --from-file target/*-runner.jar --follow

4. Deploy

   ```
   oc new-app people && oc expose svc/people
   ```

   Check the rollout

	   oc rollout status -w deployment/people

### Test the application

- Get the route

	  PEOPLE_ROUTE_URL=$(oc get route people -o=template --template='{{.spec.host}}')

  or:

    PEOPLE_ROUTE_URL=$(oc get route discount -o jsonpath='{.spec.host}')

- Run

	  curl http://${PEOPLE_ROUTE_URL}/hello

### Health check config

	oc set probe deployment/people --readiness --initial-delay-seconds=30 --get-url=http://:8080/health/ready

	oc set probe deployment/people --liveness --initial-delay-seconds=30 --get-url=http://:8080/health/live

Testcontainers with podman
---------------------------------------------------------

**Warning** this procedure was tested in Fedora 34 using _Fish shell_.

Requirements:

```
podman-3.4.0-1.fc34.x86_64
podman-docker-3.4.0-1.fc34.noarch
```

Disable the registry prompt by setting the `short-name-mode="disabled"` configuration property of Podman in `/etc/containers/registries.conf`

Start Podman service to listen on socket and grant access to users:

```sh
sudo systemctl start podman.socket
systemctl --user enable podman.socket --now
```

The following commands to check that all is up and running:

```
podman-remote info
curl -H "Content-Type: application/json" --unix-socket /run/user/(id -u)/podman/podman.sock http://localhost/_ping
```

Set the following environment variables (Fish shell way):

```sh
set DOCKER_HOST unix:///run/user/(id -u)/podman/podman.sock
set TESTCONTAINERS_RYUK_DISABLED true
```

Create `~/.testcontainers.properties` and the following properties:

```
docker.host = unix\:///run/user/1000/podman/podman.sock
ryuk.container.privileged = true
```

Enable `app` in selinux?

See:

- https://github.com/quarkusio/quarkusio.github.io/blob/aeeeaf3a4e68012ea8cbefb1e3bf9e1a6a6b376f/_posts/2021-11-02-quarkus-devservices-testcontainers-podman.adoc

- https://www.redhat.com/sysadmin/podman-docker-compose

- https://www.testcontainers.org/features/configuration/