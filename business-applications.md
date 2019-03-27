Business Applications
============================================================

Generate the project structure
------------------------------------------------------------

1. Wizard in [start.jbpm.org]()
2. CLI: https://www.npmjs.com/package/jba-cli
3. JHipster: https://www.npmjs.com/package/generator-jba
4. VSC (ide): https://marketplace.visualstudio.com/items?itemName=tsurdilovic.jbavsc

Business Central for monitoring
------------------------------------------------------------

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
    kieserver.controllers=ws://localhost:8080/business-central/websocket/controller
    ```

Resources
------------------------------------------------------------

- [youtube videos](https://www.youtube.com/user/tsurdilovic/videos?view_as=subscriber)
- [example](https://github.com/BootstrapJBPM)
- [jBPM Documentation](https://docs.jboss.org/jbpm/release/7.19.0.Final/jbpm-docs/html_single/#_businessappoverview)