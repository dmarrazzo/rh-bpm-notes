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

Blogs
------------------------------------------------

[Quarkus: Modernize “helloworld” JBoss EAP quickstart, Part 1](https://developers.redhat.com/blog/2019/11/07/quarkus-modernize-helloworld-jboss-eap-quickstart-part-1/)
[Quarkus: Modernize “helloworld” JBoss EAP quickstart, Part 2](https://developers.redhat.com/blog/2019/11/08/quarkus-modernize-helloworld-jboss-eap-quickstart-part-2/)

Common configurations
------------------------------------------------

- http port listener:

  ```
  quarkus.http.port=8090
  ```

OpenAPI
------------------------------------------------

- Add extension:

      mvn quarkus:add-extension -Dextensions="quarkus-smallrye-openapi"

- Properties:

      quarkus.swagger-ui.always-include=true
