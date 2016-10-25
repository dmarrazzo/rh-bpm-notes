Other useful information
========================

## JBoss EAP 6.4

### Password
to add user adn change password use

    add-user.sh

### Relax password strength enforcement

Change `bin\add-user.properties`:

    password.restriction=RELAX

## stop EAP

    ./jboss-cli.sh -c --command=":shutdown()"

## Configuration

EAP configuration is in:

    <JBOSSS_HOME>/standalone/configuration/standalone.xml


System properties:

    <JBOSSS_HOME>/bin/standalone.conf

e.g.

    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.ip=127.0.0.1 "
    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.port=3306 "
    JAVA_OPTS="$JAVA_OPTS -Dmysql.bpms.schema=bpms "

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
