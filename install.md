# Install

## Installation

**Prerequisite**: JDK, JBoss EAP 6.4.8

Launch:

    $ java -jar jboss-brms-6.3.0.GA-installer.jar

### Installing on EAP 7

- Download the deployable for EAP7
- extract it and adjust permissions

  chmod 775 jboss-eap-7.0

- copy the content to the EAP7 home
- add the users

        ./add-user.sh -a -u bpmsAdmin -p password --role admin,developer,analyst,user,manager,kie-server,rest-all,Administrators

- add the roles
- listen all interface:

    - add the interface:

            <interfaces>  

               <!-- Equivalent of -b 0.0.0.0 -->  

                  <interface name="any">  
                       <any-address/>  
                  </interface>  
            </interfaces>  

    - change the standard-sockets binding
    
        <socket-binding-group name="standard-sockets" default-interface="any" ...>

    - configure maven




## Users

To add a user use the EAP command line:

    ./add-user.sh -a -u bpmsAdmin -p password --role admin,developer,analyst,user,manager,kie-server,rest-all,Administrators

Users are listed in the following file:

    ~/<EAP_HOME>/standalone/configuration/application-users.properties

Roles are defined in the following file:

    ~/<EAP_HOME>/standalone/configuration/application-roles.properties

Example:

    bpmsAdmin=admin,developer,analyst,user,manager,kie-server,rest-all,Administrators

## Enable kie server

Uncomment properties in standalone.xml
Add user:

   ./add-user.sh -a -u controllerUser -p "controllerUser1234;" --role kie-server,rest-all


### Assigning role to projects


https://github.com/jboss-gpe-ref-archs/bpm_deployments/blob/master/doc/multi-tenant-bpm.adoc


## Database / datasource
By default, out-of-the-box it points to a datasource java:jboss/datasources/ExampleDS which is configured to use H2 data store in standalone*.xml files.

The default configuration H2 is configured in mem, so every restart you get a fresh DB.

To be precise, following are the two places where the `java:jboss/datasources/ExampleDS datasource` is used.


    business-central.war/WEB-INF/classes/META-INF/persistence.xml
    dashbuilder.war/WEB-INF/jboss-web.xml

Here are the changes that need to be done, in order to configure BPMS 6 to use an external Database.
business-central.war/WEB-INF/classes/META-INF/persistence.xml

    Create a new datasource and install the respective JDBC driver by following the instructions from EAP 6 Documentation .
    The default used H2 database specific datasource configuration looks like the following. Using modular approach to install the JDBC driver would be ideal for ease of configurations later.

Raw

      <subsystem xmlns="urn:jboss:domain:datasources:1.1">
         <datasources>
            <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
               <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1</connection-url>
               <driver>h2</driver>
               <security>
                  <user-name>sa</user-name>
                  <password>sa</password>
               </security>
            </datasource>
            <drivers>
               <driver name="h2" module="com.h2database.h2">
                  <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
               </driver>
            </drivers>
         </datasources>


### Persistent H2 store

    <connection-url>jdbc:h2:h2-filestore;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>

**TCP variant**

    <connection-url>jdbc:h2:tcp://localhost/~/h2tcp-filestore;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>



## Updating / Patching BPM Suite

patches are not comulative

1. Unzip `jboss-bpmsuite-6.3.2-patch.zip`
2. Issue `./apply-updates.sh <EAP_HOME> eap7.x`
3. Extract the maven repo update in the previous repo directory



  export BPMS_HOME=/opt/jboss/bpms/jboss-eap-7.0/
  export BPMS_INST=/opt/jboss/
  export BPMS_PATCH_WILDCARD=jboss-bpmsuite-6.4.?-*
  for f in $BPMS_INST$BPMS_PATCH_WILDCARD; do \
    unzip -qo $f -d $BPMS_INST && \
    (cd ${f::-4} && exec ./apply-updates.sh $BPMS_HOME eap7.x); \
  done 

### Updating JBoss EAP

Downlaod the EAP patched and unzip the bundle to get the incremental fix.
E.g. jboss-eap-6.4.8.CP.zip
Lauch the CLI

    $ ./jboss-cli.sh

In the cli launch the patch command

    [standalone@localhost:9999 /] patch apply /path/to/downloaded-patch.zip

otherwise:

  $ ./jboss-cli.sh --commands=patch\ apply\ <dir>/jboss-eap-7.0.5-patch.zip


# Clustering

For the Business Central clustering look at Installation Guide.

Here some basic information to cluster the kie-server:

- same database
- setting Quartz

## Clustering JMS Active MQ on EAP 7

???


<!--cluster jms-->

<pooled-connection-factory name="activemq-ra" transaction="xa" entries="java:/JmsXA java:jboss/DefaultJMSConnectionFactory" connectors="http-connector"/>
                <broadcast-group name="my-broadcast-group" connectors="http-connector" socket-binding="messaging-group"/>
                <discovery-group name="my-discovery-group" refresh-timeout="10000" socket-binding="messaging-group"/>


# Configuration for High Performances

- DisabledFollowOnLockOracle10gDialect

# Integrating SSO

[SSO Tison article](https://github.com/jboss-gpe-ref-archs/bpms_rhsso/blob/master/doc/bpms_rhsso.adoc)


# Problems
## Cannot login in Business Central (workbench)

Create a new file `/standalone/deployments/business-central.war/WEB-INF/classes/ErraiService.properties` with the following content:

    errai.bus.enable_sse_support=false

add in `standalone.conf` the following line:

    JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"

If the problem persist, try to disable the workstation antivirus.

Reference:

[https://access.redhat.com/solutions/1183473]()

## How to access maven repository?

Add username/password in `~/.m2/settings.xml`

    <server>
      <id>guvnor-m2-repo</id>
      <username>admin</username>
      <password>admin</password>
      <configuration>
        <wagonProvider>httpclient</wagonProvider>
        <httpConfiguration>
          <all>
            <usePreemptive>true</usePreemptive>
          </all>
        </httpConfiguration>
      </configuration>
    </server>

Reference:
[https://access.redhat.com/solutions/703423]()

## System properties


 * org.uberfire.nio.git.dir: Location of the directory .niogit. Default: working directory
 * org.uberfire.nio.git.daemon.enabled: Enables/disables git daemon. Default: true
 * org.uberfire.nio.git.daemon.host: If git daemon enabled, uses this property as local host identifier. Default: localhost
 * org.uberfire.nio.git.daemon.port: If git daemon enabled, uses this property as port number. Default: 9418
 * org.uberfire.nio.git.ssh.enabled: Enables/disables ssh daemon. Default: true
 * org.uberfire.nio.git.ssh.host: If ssh daemon enabled, uses this property as local host identifier. Default: localhost
 * org.uberfire.nio.git.ssh.port: If ssh daemon enabled, uses this property as port number. Default: 8001
 * org.uberfire.nio.git.ssh.cert.dir: Location of the directory .security where local certtificates will be stored. Default: working directory
 * org.uberfire.metadata.index.dir: Place where Lucene .index folder will be stored. Default: working directory
 * org.uberfire.cluster.id: Name of the helix cluster, for example: kie-cluster
 * org.uberfire.cluster.zk: Connection string to zookeeper. This is of the form host1:port1,host2:port2,host3:port3, for example: localhost:2188
 * org.uberfire.cluster.local.id: Unique id of the helix cluster node, note that ':' is replaced with `'_'`, for example: node1_12345
 * org.uberfire.cluster.vfs.lock: Name of the resource defined on helix cluster, for example: kie-vfs
 * org.uberfire.cluster.autostart: Delays VFS clustering until the application is fully initialized to avoid conflicts when all cluster members create local clones. Default: false
 * org.uberfire.sys.repo.monitor.disabled: Disable configuration monitor (do not disable unless you know what you're doing). Default: false
 * org.uberfire.secure.key: Secret password used by password encryption. Default:  * org.uberfire.admin
 * org.uberfire.secure.alg: Crypto algorithm used by password encryption. Default: PBEWithMD5AndDES
 * org.guvnor.m2repo.dir: Place where Maven repository folder will be stored. Default: working-directory/repositories/kie
 * org.kie.example.repositories: Folder from where demo repositories will be cloned. The demo repositories need to have been obtained and placed in this folder. Demo repositories can be obtained from the kie-wb-6.1.0-SNAPSHOT-example-repositories.zip artifact. This System Property takes precedence over org.kie.demo and org.kie.example. Default: Not used.
 * org.kie.demo: Enables external clone of a demo application from GitHub. This System Property takes precedence over org.kie.example. Default: true
 * org.kie.example: Enables example structure composed by Repository, Organization Unit and Project. Default: false

## Internal git ssh

to connect to an external ssh?

     * org.uberfire.nio.git.ssh.cert.dir

default working dir

then .security

there sha_id?

Configure the passphrase

     * org.uberfire.nio.git.ssh.passphrase

## Internal git is not accessible

Look for the <system-properties> tag and add the following:

    <property name="org.uberfire.nio.git.ssh.host" value="yourserverdomain"/>

Not sure if the following is useful:

    <property name="org.uberfire.nio.git.daemon.host" value="yourserverdomain"/>

Generic Solution?

        <property name="org.uberfire.nio.git.daemon.host" value="0.0.0.0"/>
        <property name="org.uberfire.nio.git.ssh.host" value="0.0.0.0"/>



## Internal git offer ssh-dss
Issue https://issues.jboss.org/browse/RHBRMS-243

####workaround:

Add to ~/.ssh/config the followings:

    Host *
        VerifyHostKeyDNS no
        StrictHostKeyChecking no
        HostKeyAlgorithms +ssh-dss
        UserKnownHostsFile /dev/null


Old ssh does not accept `+`, so change with:

    HostKeyAlgorithms ssh-dss


## LDAP

### Authentication on JBoss EAP

```
<security-domain name="myLdapDomain">
  <authentication>
      <login-module code="org.jboss.security.auth.spi.LdapExtLoginModule" flag="required">
          <module-option name="java.naming.factory.initial" value="com.sun.jndi.ldap.LdapCtxFactory"/>
          <module-option name="java.naming.provider.url" value="ldap://ldap_server_ip:389"/>
          <module-option name="bindDN" value="cn=queryUser,cn=Users,dc=mydomain,dc=com"/>
          <module-option name="bindCredential" value="queryUserPassword"/>
          <module-option name="baseCtxDN" value="cn=Users,dc=mydomain,dc=com"/>
          <module-option name="baseFilter" value="(userPrincipalName={0})"/>
          <module-option name="rolesCtxDN" value="cn=Users,dc=mydomain,dc=com"/>
          <module-option name="roleFilter" value="(userPrincipalName={0})"/>
          <module-option name="roleAttributeID" value="memberOf"/>
          <module-option name="roleNameAttributeID" value="cn"/>
          <module-option name="roleAttributeIsDN" value="true"/>
          <module-option name="allowEmptyPasswords" value="true"/>
          <module-option name="Context.REFERRAL" value="follow"/>
          <module-option name="throwValidateError" value="true"/>
          <module-option name="searchScope" value="SUBTREE_SCOPE"/>
      </login-module>
      <login-module code="org.jboss.security.auth.spi.RoleMappingLoginModule" flag="optional">
          <module-option name="rolesProperties" value="roles.properties"/>
          <module-option name="replaceRole" value="false"/>
      </login-module>
  </authentication>
</security-domain>
```

### Authorization on BPM side

For some features related to querying the users-groups you must change the default UserGroupCallBack defined into business-central/WEB-INF/beans.xml
<alternatives>
    <class>org.jbpm.services.cdi.producer.JAASUserGroupInfoProducer</class>
  </alternatives>
change to
<alternatives>
    <class>org.jbpm.services.cdi.producer.LDAPUserGroupInfoProducer</class>
  </alternatives>

Additional you need to define the system property jbpm.usergroup.callback.properties and point to your propertie file (WEB-INF/jbpm.usergroup.callback.properties)
