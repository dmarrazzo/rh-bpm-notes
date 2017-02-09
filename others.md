Other useful information
========================

## Developing

- eclipse UML visualizer: ObjectAid UML Explorer

## JBoss EAP 6.4

### Password
to add user adn change password use

    add-user.sh

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

e.g.

    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.ip=127.0.0.1"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.port=3306"
    JAVA_OPTS="$JAVA_OPTS -Dmysql.bpms.schema=bpms"

### Multiple instances of EAP

Guidelines on how to run multiple instances by copying standalone directory in JBoss EAP.

Copy standalone directory. For example, to node1 and node2 like:

    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node1
    cp  -a  $JBOSS_HOME/standalone $JBOSS_HOME/node2
    
Start each instance, using the start up scripts in $JBOSS_HOME/bin, by specifying the node name, ip address (or a port shift) and server directory:

    ./standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=$JBOSS_HOME/node1 -c standalone.xml -b 10.20.30.40 -bmanagement 10.20.30.4

with the same IP:

    ./standalone.sh -Djboss.node.name=kie-node1 -Djboss.server.base.dir=$JBOSS_HOME/kie-node1 -c standalone.xml -Djboss.socket.binding.port-offset=1


### Mysql DB Driver

    JAR: <JBOSSS_HOME>/modules/com/mysql/main/mysql-connector-java.jar

### Logging

configuration in JBoss console:

- Core -> Logging -> Log categories
- add the package

usage in the code (it's possible to inject it):

    import org.slf4j.Logger;
    (...)
    private Logger log = LoggerFactory.getLogger(getClass());

### How to increase heap size

Edit `bin/standalone.sh` add:

    JAVA_OPTS="-Xmx2048m"


## Internal repositories

stored where eap is launched:

    $EAP_HOME/bin/

- git repo `.niogit`
- maven `repositories/kie`
- index `.index`
- h2 database

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
    Database URL: jdbc:h2:tcp://localhost/~/test 
