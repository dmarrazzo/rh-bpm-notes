Other Related Products Information
==================================

Developing
------------------------------------

### eclipse UML visualizer: ObjectAid UML Explorer

In the 'Add Repository' dialog, enter this information and press 'OK':
- Name: `ObjectAid UML Explorer`
- URL: `http://www.objectaid.com/update/current`

### Web UI development

ReactJS is a great library to develop modern UIs:

- [React Tutorial - Learn React - React Crash Course](https://www.youtube.com/watch?v=Ke90Tje7VS0)


JBoss EAP 7.x
------------------------------------

### Configure GMail

**WARNING**: To use Gmail SMTP, you have to enable in your google account the flag "allow less secure app"

Define the mail session:

    <mail-session name="jbpmmail" jndi-name="java:jboss/mail/jbpmMailSession">
        <smtp-server outbound-socket-binding-ref="gmail-smtp" ssl="true" username="user@gmail.com" password="secretpwd">
        </smtp-server>
    </mail-session>

Configure outbound connection:

	<outbound-socket-binding name="gmail-smtp">
	    <remote-destination host="smtp.gmail.com" port="465"/>
	</outbound-socket-binding>


JBoss EAP 6.4
------------------------------------


### Install

    java -jar <eap_jar> -console

### Updating JBoss EAP

Current version:

     ./standalone.sh -v

Downlaod the EAP patched and unzip the bundle to get the incremental fix.

Launch the CLI:

    $ ./jboss-cli.sh 

In the cli launch the patch command

    [standalone@localhost:9999 /] patch apply /path/to/downloaded-patch.zip
    
### Password

to add user or change password use

    ./add-user.sh -a user password

### Relax password strength enforcement

Change `bin\add-user.properties`:

    password.restriction=RELAX

### Stop EAP

    ./jboss-cli.sh -c --command=":shutdown()"

### Configuration

EAP configuration is in:

    <JBOSSS_HOME>/standalone/configuration/standalone.xml


System properties:

    <JBOSSS_HOME>/bin/standalone.conf

### Skip application deployment

Add in the deployment directory a file

    <name>.skipdeploy
    

### Multiple instances of EAP

Guidelines on how to run multiple instances by copying standalone directory in JBoss EAP.

Copy standalone directory. For example, to node1 and node2 like:

    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node1
    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node2
    
Start each instance, using the start up scripts in $JBOSS_HOME/bin, by specifying the node name, ip address (or a port shift) and server directory:

    ./standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=$JBOSS_HOME/node1 -c standalone.xml -b 10.20.30.40 -bmanagement 10.20.30.4

with the same IP:

    ./standalone.sh -Djboss.node.name=kie-node1 -Djboss.server.base.dir=$JBOSS_HOME/kie-node1 -c standalone.xml -Djboss.socket.binding.port-offset=1

### Adding a shared library to the EAP configuration

[class loading and modules](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.1/html/configuration_guide/overview_of_class_loading_and_modules#modules)

- Add the module:

        ./jboss-cli.sh 
    
        module add --name=<module-name> --resources=<absolute_path>/library.jar

- Define a global module, so it will be shared among all the other modules

        connect
        /subsystem=ee:list-add(name=global-modules,value={name=<module-name>})

- Another option is to declare the dependency in a specific WAR file adding the following line to META-INF/MANIFEST.MF

        Dependencies: <module-name>

### Using environment variable in data source configuration in JBoss EAP 6/7

Environment variable `server`

```xml
<datasource jndi-name="java:jboss/datasources/NewDS" pool-name="NewDS" enabled="true" use-java-context="true" use-ccm="true">
    <connection-url>jdbc:db2://**${env.server}**:3306/aaa</connection-url>
```

[https://access.redhat.com/solutions/744843]()


### Logging

configuration in JBoss console:

- Core -> Logging -> Log categories
- add the package

Command line:

    /subsystem=logging/logger=package.name:change-log-level(level=NEW_LOGGING_LEVEL)

usage in the code (it's possible to inject it):

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    (...)
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

#### Configure in a project

In the POM file add:

  <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.2</version>
      <scope>runtime</scope>
  </dependency>
  <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.0.9</version>
  </dependency>

Add in the classpath `logback.xml` (or `logback-test.xml`):

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
    
      <appender name="consoleAppender" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
          <pattern>%date{HH:mm:ss.SSS} [%thread] %-5level %class{36}.%method:%line - %msg%n</pattern>
        </encoder>
      </appender>
    
      <logger name="org.kie" level="info"/>
      <logger name="org.drools" level="info"/>
      <logger name="org.jbpm" level="info"/>
    
    
      <root level="debug">
        <appender-ref ref="consoleAppender" />
      </root>
    
    </configuration>

### How to increase heap size

Edit `bin/standalone.conf` and change:

    JAVA_OPTS="-Xms2303m -Xmx4303m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=1024m -Djava.net.preferIPv4Stack=true"

### Frameworks supported by EAP

EAP 6.4 - Hibernate 4.2.18

#### SQL Dialects (hibernate.dialect)

    DB2	org.hibernate.dialect.DB2Dialect
    DB2 AS/400	org.hibernate.dialect.DB2400Dialect
    DB2 OS390	org.hibernate.dialect.DB2390Dialect
    Firebird	org.hibernate.dialect.FirebirdDialect
    FrontBase	org.hibernate.dialect.FrontbaseDialect
    H2 Database	org.hibernate.dialect.H2Dialect
    HypersonicSQL	org.hibernate.dialect.HSQLDialect
    Informix	org.hibernate.dialect.InformixDialect
    Ingres	org.hibernate.dialect.IngresDialect
    Interbase	org.hibernate.dialect.InterbaseDialect
    Mckoi SQL	org.hibernate.dialect.MckoiDialect
    Microsoft SQL Server 2000	org.hibernate.dialect.SQLServerDialect
    Microsoft SQL Server 2005	org.hibernate.dialect.SQLServer2005Dialect
    Microsoft SQL Server 2008	org.hibernate.dialect.SQLServer2008Dialect
    Microsoft SQL Server 2012	org.hibernate.dialect.SQLServer2008Dialect
    MySQL5	org.hibernate.dialect.MySQL5Dialect
    MySQL5 with InnoDB	org.hibernate.dialect.MySQL5InnoDBDialect
    MySQL with MyISAM	org.hibernate.dialect.MySQLMyISAMDialect
    Oracle (any version)	org.hibernate.dialect.OracleDialect
    Oracle 9i	org.hibernate.dialect.Oracle9iDialect
    Oracle 10g	org.hibernate.dialect.Oracle10gDialect
    Oracle 11g	org.hibernate.dialect.Oracle10gDialect
    Pointbase	org.hibernate.dialect.PointbaseDialect
    PostgreSQL	org.hibernate.dialect.PostgreSQLDialect
    PostgreSQL 9.2	org.hibernate.dialect.PostgreSQL82Dialect
    Postgres Plus Advanced Server	org.hibernate.dialect.PostgresPlusDialect
    Progress	org.hibernate.dialect.ProgressDialect
    SAP DB	org.hibernate.dialect.SAPDBDialect
    Sybase	org.hibernate.dialect.SybaseASE15Dialect
    Sybase 15.7	org.hibernate.dialect.SybaseASE157Dialect
    Sybase Anywhere	org.hibernate.dialect.SybaseAnywhereDialect
    

EAP 7.0 - Hibernate 5.0.9

[JBoss Enterprise Application Platform Component Details](https://access.redhat.com/articles/112673)

Internal repositories
------------------------------------

stored where eap is launched:

    $EAP_HOME/bin/

- git repo `.niogit`
- maven `repositories/kie`
- index `.index`
- h2 database

Default document implementation store files in the file system:

    .doc
    business-central.war/WEB-INF/tmp

### Webhooks

In order to configure git hook in the business central, add the following system property to point the script folder.

```xml
<property name="org.uberfire.nio.git.hooks" value="/home/donato/apps/rhpam-73/standalone/data/kie/git/hooks"/>
```

Create the *post-commit* `script`:

```bash
#!/bin/bash

java -jar -Dsync.mode=on_sync -Dbc.url=myapp-rhpamcentr-rhpam-user1.apps.rotterdam-a2a2.openshiftworkshop.com:8001 -Dgh.username={your-user} -Dgh.password={your-password-or-token} /opt/eap/standalone/data/kie/git/hooks/bc-github-githook-1.0.0-Beta1.jar
```

To test webhooks, there is a really useful utility:
[Ngrok](http://ngrok.com/)

ngrok command line for tcp connections:

    ngrok tcp 8001

Remote debug
------------------------------------

**By default debug mode is disabled.**
Launch:

    ./standalone.sh --debug

or change sh with: `DEBUG_MODE=true`

Check `<JBOSS_HOME>/bin/standalone.sh`

In Eclipse create a new Debug configuration using the template `Remote Java Application`:

- Connection Type: Standard
- Port: 8787 (standard port of JBoss EAP)

Databases
------------------------------------

### H2

TCP config:

java -cp h2*.jar org.h2.tools.Server -tcp

To remotely connect to a database using the TCP server, use the following driver and database URL:

    JDBC driver class: org.h2.Driver
    Database URL: jdbc:h2:tcp://localhost/~/h2-net

[h2-console in EAP](https://developers.redhat.com/quickstarts/eap/h2-console/)

### Mysql DB Driver

    JAR: <JBOSSS_HOME>/modules/com/mysql/main/mysql-connector-java.jar



    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.ip=127.0.0.1"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.port=3306"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.bpms.schema=bpms"


Infinispan
------------------------------------

[Real-time data and statistics on the open hybrid cloud with Quarkus and Infinispan](https://developers.redhat.com/devnation/tech-talks/data-quarkus-infinispan)

CI/CD
==================================

the post-commit hook script check if the commit touched the POM file, and if it's the case you could also check if it was a version bump, then the script pushes the changes triggering the pipeline.

Further Interesting information
==================================

- [Simian (Similarity Analyser) identifies duplication in code and rules](https://www.harukizaemon.com/simian/index.html)
- [Bridge Java and .net](http://jni4net.com/)
- [What is container management and why is it important?](https://searchitoperations.techtarget.com/definition/container-management-software)

Security
------------------------------------

- [OAuth 2.0 and OpenID Connect (in plain English)](https://www.youtube.com/watch?v=996OiexHze0&t=1s)


Access to the remote server using SSH as a proxy server
------------------------------------

Port forwarding comes to the rescue:

```
ssh -N -L 8090:remote-host:8080 user@remote-host
```

- 8090 is the local port
- 8080 is the remote port

Low code
------------------------------------


Interesting projects:

- Hasura by far, lets you point-and-click build your database and table relationships with a web dashboard and autogenerates a full GraphQL CRUD API with permissions you can configure and JWT/webhook auth baked-in.
https://hasura.io/

I've been able to build in a weekend no-code what would've taken my team weeks or months to build by hand, even with something as productive as Rails. It automates the boring stuff and you just have to write single endpoints for custom business logic, like "send a welcome email on sign-up" or "process a payment".

It has a database viewer, but it's not the core of the product, so I use Forest Admin to autogenerate an Admin Dashboard that non-technical team members can use:

https://www.forestadmin.com/

With these two, you can point-and-click make 80% of a SaaS product in almost no time.

I wrote a tutorial on how to integrate Hasura + Forest Admin, for anyone interested:

http://hasura-forest-admin.surge.sh

For interacting with Hasura from a client, you can autogenerate fully-typed & documented query components in your framework of choice using GraphQL Code Generator:

https://graphql-code-generator.com/

Then I usually throw Metabase in there as a self-hosted Business Intelligence platform for non-technical people to use as well, and PostHog for analytics:

https://www.metabase.com/

https://posthog.com/

All of these all Docker Containers, so you can have them running locally or deployed in minutes.

This stack is absurdly powerful and productive.


Interesting Reading
===================

[Open Source is not a business model](https://anonymoushash.vmbrasseur.com/2018/08/24/open-source-is-not-a-business-model-and-your-business-will-fail-if-you-think-that-it-is/)

[Fuse Tutorial](https://gitlab.com/rh-emea-ssa-fuse-tutorial/fis-rest-composite)

[Thinking in Events: From Databases to Distributed Collaboration Software](https://youtu.be/72W_VvFRqc0)

[Distributed transaction patterns for microservices compared](https://developers.redhat.com/articles/2021/09/21/distributed-transaction-patterns-microservices-compared)