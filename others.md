# Other useful information

## JBoss EAP 6.4 

### Password
to add user adn change password use 

    add-user.sh

### Relax password strength enforcement

Change `bin\add-user.properties`:

    password.restriction=RELAX

### Configuration

    <JBOSSS_HOME>/standalone/configuration/standalone.xml
### System properties

    <JBOSSS_HOME>/bin/standalone.conf

e.g.

    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.ip=127.0.0.1 "
    JAVA_OPTS="$JAVA_OPTS -Dmysql.host.port=3306 "
    JAVA_OPTS="$JAVA_OPTS -Dmysql.bpms.schema=bpms "
    
### Mysql DB Driver

    JAR: <JBOSSS_HOME>/modules/com/mysql/main/mysql-connector-java.jar

### How to increase heap size

Edit `bin/standalone.sh` add:

    JAVA_OPTS="-Xmx2048m"
