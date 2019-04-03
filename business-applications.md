Business Applications
============================================================

Generate the project structure
------------------------------------------------------------

1. Wizard in [start.jbpm.org]()
2. CLI: https://www.npmjs.com/package/jba-cli
3. JHipster: https://www.npmjs.com/package/generator-jba
4. VSC (ide): https://marketplace.visualstudio.com/items?itemName=tsurdilovic.jbavsc

### Archetype


```bash
mvn archetype:generate -B -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-model-archetype -DarchetypeVersion=7.18.0.Final-redhat-00002 -DgroupId=com.company -DartifactId=test-model -Dversion=1.0-SNAPSHOT -Dpackage=com.company.model

mvn archetype:generate -B -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-kjar-archetype -DarchetypeVersion=7.18.0.Final-redhat-00002 -DgroupId=com.company -DartifactId=test-kjar -Dversion=1.0-SNAPSHOT -Dpackage=com.company

mvn archetype:generate -B -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-service-spring-boot-archetype -DarchetypeVersion=7.18.0.Final-redhat-00002 -DgroupId=com.company -DartifactId=test-service -Dversion=1.0-SNAPSHOT -Dpackage=com.company.service -DappType=bpm
```

Business Central for development
------------------------------------------------------------

Configure the properties in  `business-application-service/src/main/resources/application-dev.properties`

```bash
kieserver.controllers=http://localhost:8080/business-central/rest/controller
```

Start:

```bash
mvn clean package
./launch-dev.sh
```

Otherwise in one single line:

```bash
./launch-dev.sh clean package
```

Business Central for monitoring
------------------------------------------------------------

Configure the properties in  `business-application-service/src/main/resources/application.properties`

```bash
kieserver.controllers=http://<bc-service-name>:8080/business-central/rest/controller
```

Run unmanaged

```bash
mvn clean spring-boot:run -Dorg.kie.executor.running.max=1 -Dorg.kie.server.startup.strategy=LocalContainersStartupStrategy
```

The `xml` file containing the `kie-server-state` configuration must include the list of containers to deploy at start up

Configure controller credentials
------------------------------------------------------------

In the business application, you can configure the user/password to connect with the controller (Business Central) through the following *system properties*:

    org.kie.server.controller.user=<user>
    org.kie.server.controller.pwd=<password>

There are two ways to pass these *system properties*:

- edit the server `xml` (e.g. `business-application-service-dev.xml`)

    ```xml
    <config-item>
        <name>org.kie.server.controller.user</name>
        <value>actual_user</value>
        <type>java.lang.String</type>
    </config-item>
    <config-item>
        <name>org.kie.server.controller.pwd</name>
        <value>actual_password</value>
        <type>java.lang.String</type>
    </config-item>
    ```

- pass the properties in the java command line, edit `launch-dev.sh`

    ```bash
    java -Dspring.profiles.active=dev -Dorg.kie.server.controller.user=<user> -Dorg.kie.server.controller.pwd=<password> -jar "$executable"
    ```

Configure controller communication protocol
------------------------------------------------------------

By default, business applications are configured to use a bi-directional REST/HTTP communication.

This way does not work in a kubernates / openshift environment where multiple instances are exposed through the same service endpoint.

Read more here: [Managed KIE Server gets ready for the cloud](http://mswiderski.blogspot.com/2017/08/managed-kie-server-gets-ready-for-cloud.html).

In order to set up the websocket protocol in the business application:

- add the dependency in the `pom.xml` file:

    ```xml
    <dependency>
        <groupId>org.kie.server</groupId>
        <artifactId>kie-server-controller-websocket-client</artifactId>
        <version>${version.org.kie}</version>
    </dependency>
    ```

- change the controller url

    ```bash
    kieserver.controllers=ws://<bc-service-name>:8080/business-central/websocket/controller
    ```

Running in OpenShift
------------------------------------------------------------

Change `pom.xml`:

```xml
<env>
    <M2_HOME>/opt/jboss/.m2</M2_HOME>
    <JAVA_OPTIONS>-Dkie.maven.settings.custom=/opt/jboss/.m2/settings.xml -Dorg.guvnor.m2repo.dir=/opt/jboss/.m2/repository</JAVA_OPTIONS>
</env>
<ports>
    <port>8090</port>
</ports>
<runCmds>
    <run>chgrp -Rf root /opt/jboss &amp;&amp; chmod -Rf g+w /opt/jboss</run>
    <run>chgrp -Rf root /deployments &amp;&amp; chmod -Rf g+w /deployments</run>
</runCmds>
```

Note: in order to define the maven repository location, regardless the user running the application make sure that this properties are passed:

```bash
-Dkie.maven.settings.custom=/opt/jboss/.m2/settings.xml -Dorg.guvnor.m2repo.dir=/opt/jboss/.m2/repository
```

Run this command to deploy in OpenShift:

```sh
./launch.sh clean install -Popenshift,h2
```

Stop it when you see this line:

```bash
[INFO] F8: Starting Build business-application-service-s2i
```

Behind the scenes, it runs `fabric8` to create a Dockerfile and the content of the image.

You can find the Dockerfile in `./target/docker/apps/business-application-service/1.0-SNAPSHOT/build/Dockerfile`.
The image will contains:

- the service jar that contains the kieserver runtime
- a standalone local maven repository with the kjar and other supporting jars
- the kieserver configuration in business-application-service.xml (a good alternative is to bind this file with a configmap)

```ruby
FROM fabric8/java-jboss-openjdk8-jdk
ENV JAVA_OPTIONS="-Dkie.maven.settings.custom=/opt/jboss/.m2/settings.xml -Dorg.guvnor.m2repo.dir=/opt/jboss/.m2/repository" M2_HOME=/opt/jboss/.m2
EXPOSE 8090
COPY maven /
RUN chgrp -Rf root /opt/jboss && chmod -Rf g+w /opt/jboss
RUN chgrp -Rf root /deployments && chmod -Rf g+w /deployments
USER jboss:jboss:jboss
```

Unfortunately, as is, it's not working, so you have to modify to add the `root` user:

```ruby
FROM fabric8/java-jboss-openjdk8-jdk
ENV JAVA_OPTIONS="-Dkie.maven.settings.custom=/opt/jboss/.m2/settings.xml -Dorg.guvnor.m2repo.dir=/opt/jboss/.m2/repository" M2_HOME=/opt/jboss/.m2
EXPOSE 8090
COPY maven /
USER root
RUN chgrp -Rf root /opt/jboss && chmod -Rf g+w /opt/jboss
RUN chgrp -Rf root /deployments && chmod -Rf g+w /deployments
USER jboss:jboss:jboss
```

Create a new build for your application:

```sh
$ oc new-build --strategy docker --binary --name busapp
```

Start a binary build using the local directory’s content:

```bash
$ oc start-build busapp --from-dir . --follow
```

Deploy the application using new-app, then create a route for it:

```bash
$ oc new-app busapp
$ oc expose svc/busapp
```

Get the host name for your route and navigate to it:

```bash
$ oc get route busapp
```

Related informations:

[https://docs.openshift.com/container-platform/3.11/creating_images/guidelines.html]()
[https://docs.okd.io/latest/dev_guide/dev_tutorials/binary_builds.html]()

Workaround for user recognition problem:
[https://github.com/RHsyseng/container-rhel-examples/blob/master/starter-arbitrary-uid/Dockerfile]()


Resources
------------------------------------------------------------

- [youtube videos](https://www.youtube.com/user/tsurdilovic/videos?view_as=subscriber)
- [example](https://github.com/BootstrapJBPM)
- [jBPM Documentation](https://docs.jboss.org/jbpm/release/7.19.0.Final/jbpm-docs/html_single/#_businessappoverview)

https://github.com/BootstrapJBPM/jbpm-bootstrap-service/blob/master/pom.xml#L257-L260