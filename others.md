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


    <JBOSSS_HOME>/standalone/configuration/standalone.xml

### System properties

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
Edit `<JBOSSS_HOME>/bin/standalone.sh`
and change:

```
# By default debug mode is disable.
DEBUG_MODE=true
```
In Eclipse create a new Debug configuration using the template `Remote Java Application`:

- Connection Type: Standard
- Port: 8787 (standard port of JBoss EAP)
