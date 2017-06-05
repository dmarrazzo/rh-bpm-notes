Other useful information
========================

## Developing

- eclipse UML visualizer: ObjectAid UML Explorer

## JBoss EAP 6.4

### Install

    java -jar <eap_jar> -console

### Updating JBoss EAP

Downlaod the EAP patched and unzip the bundle to get the incremental fix.
E.g. jboss-eap-6.4.8.CP.zip
Lauch the CLI

    $ ./jboss-cli.sh 

In the cli launch the patch command

    [standalone@localhost:9999 /] patch apply /path/to/downloaded-patch.zip
    
### Password
to add user or change password use

    add-user.sh -a user password

### Relax password strength enforcement

Change `bin\add-user.properties`:

    password.restriction=RELAX

### stop EAP

    ./jboss-cli.sh -c --command=":shutdown()"

### Configuration

EAP configuration is in:

    <JBOSSS_HOME>/standalone/configuration/standalone.xml


System properties:

    <JBOSSS_HOME>/bin/standalone.conf


### Multiple instances of EAP

Guidelines on how to run multiple instances by copying standalone directory in JBoss EAP.

Copy standalone directory. For example, to node1 and node2 like:

    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node1
    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node2
    
Start each instance, using the start up scripts in $JBOSS_HOME/bin, by specifying the node name, ip address (or a port shift) and server directory:

    ./standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=$JBOSS_HOME/node1 -c standalone.xml -b 10.20.30.40 -bmanagement 10.20.30.4

with the same IP:

    ./standalone.sh -Djboss.node.name=kie-node1 -Djboss.server.base.dir=$JBOSS_HOME/kie-node1 -c standalone.xml -Djboss.socket.binding.port-offset=1


### Logging

configuration in JBoss console:

- Core -> Logging -> Log categories
- add the package

usage in the code (it's possible to inject it):

    import org.slf4j.Logger;
    (...)
    private Logger log = LoggerFactory.getLogger(getClass());

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


## Internal repositories

stored where eap is launched:

    $EAP_HOME/bin/

- git repo `.niogit`
- maven `repositories/kie`
- index `.index`
- h2 database

Default document implementation store files in the file system:

    .doc
    business-central.war/WEB-INF/tmp

## Remote debug
**By default debug mode is disabled.**
Launch:

    standalone.sh --debug

or change sh with: `DEBUG_MODE=true`

Check `<JBOSS_HOME>/bin/standalone.sh`

In Eclipse create a new Debug configuration using the template `Remote Java Application`:

- Connection Type: Standard
- Port: 8787 (standard port of JBoss EAP)

## Database

### H2

TCP config:

java -cp h2*.jar org.h2.tools.Server -tcp

To remotely connect to a database using the TCP server, use the following driver and database URL:

    JDBC driver class: org.h2.Driver
    Database URL: jdbc:h2:tcp://localhost/~/h2-net


### Mysql DB Driver

    JAR: <JBOSSS_HOME>/modules/com/mysql/main/mysql-connector-java.jar



    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.ip=127.0.0.1"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.port=3306"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.bpms.schema=bpms"
