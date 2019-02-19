Install
======================================

## Graphical Installation BPM Suite

**Prerequisite**: JDK, JBoss EAP 6.4.8

Launch:

    $ java -jar jboss-brms-6.3.0.GA-installer.jar

## Installing RHDM 7

- standalone-full.xml
- kieserver prop

Sample script: 
https://github.com/jbossdemocentral/rhdm7-install-demo/blob/dm7ga/init.sh


## Installing RHPAM 7

- EAP 7.1
- add users

    ./add-user.sh -a -u bpmsAdmin -p password --role admin,process-admin,developer,analyst,user,manager,rest-all,kie-server


## Installing Red Hat Process Automation Manager

### Installing on EAP 7

- Download the deployable for EAP7
- extract it and adjust permissions

  chmod 775 jboss-eap-7.0

- copy the content to the EAP7 home
- add the users

        ./add-user.sh -a -u pamAdmin -p password --role admin,developer,analyst,user,manager,kie-server,rest-all,Administrators

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

### Disable datasource lookup in Business Central

Add the following setting in `business-central.war/WEB-INF/classes/datasource-management.properties`

    datasource.management.disableDefaultDrivers=true

known issue: 

 - [datasource.management.disableDefaultDrivers parameter does not disable default drivers in RHPAM 7.0.1](https://issues.jboss.org/browse/RHPAM-1366)

### Configure Persistence

#### Install JDBC drivers

    $ EAP_HOME/bin/jboss-cli.sh


    module add --name=MODULE_NAME --resources=PATH_TO_JDBC_JAR --dependencies=DEPENDENCIES

Example

    module add --name=com.mysql --resources=/path/to/mysql-connector-java-5.1.36-bin.jar --dependencies=javax.api,javax.transaction.api

[eap datasource](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.0/html/configuration_guide/datasource_management)

#### Oracle XA Datasource

1. Grant the permissions

    This is the EAP doc (it refer to a XA thin driver!):
    
    [Datasource management: oracle xa datasource](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.0/html/configuration_guide/datasource_management#example_oracle_xa_datasource)

        GRANT SELECT ON sys.dba_pending_transactions TO user;
        GRANT SELECT ON sys.pending_trans$ TO user;
        GRANT SELECT ON sys.dba_2pc_pending TO user;
        GRANT EXECUTE ON sys.dbms_xa TO user;

2. Install the module (Using the jboss-cli)

        module add --name=com.oracle --resources=<path>/ojdbc8.jar --dependencies=javax.api,javax.transaction.api


3. Install the driver (Using the jboss-cli)

/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle,driver-xa-datasource-class-name=oracle.jdbc.xa.client.OracleXADataSource,driver-class-name=oracle.jdbc.OracleDriver)

Do it even of the server where you installed through the Dashbuilder console, just to be sure that everything is aligned-

4. Configure the XA data source (from the Admin Console)

    URL:

        jdbc:oracle:thin:@oracleHostName:1521:serviceName
    
    Other option:
    
        url="jdbc:oracle:thin:@(DESCRIPTION=
        (LOAD_BALANCE=on)
        (ADDRESS_LIST=
         (ADDRESS=(PROTOCOL=TCP)(HOST=host1) (PORT=1521))
         (ADDRESS=(PROTOCOL=TCP)(HOST=host2)(PORT=1521)))
         (CONNECT_DATA=(SERVICE_NAME=service_name)))"

    Datasource properties (for OpenShift image):

        DATASOURCES=ORACLE
        ORACLE_DATABASE=jbpm
        ORACLE_JNDI=java:jboss/datasources/jbpm
        ORACLE_DRIVER=oracle
        ORACLE_USERNAME=jbpmuser
        ORACLE_PASSWORD=jbpmpass
        ORACLE_TX_ISOLATION=TRANSACTION_READ_UNCOMMITTED
        ORACLE_JTA=true
        ORACLE_SERVICE_HOST=1.2.3.4
        ORACLE_SERVICE_PORT=1521


    
5. RELOAD THE CONFIG
   (for jboss-cli)

        :reload

    Or restart the application server

#### Load the DDL

Download Red Hat JBoss BPM Suite 6.4.0 Supplementary Tools.

Unzip jboss-brms-bpmsuite-6.4-supplementary-tools/ddl-scripts, for example into /tmp/ddl.

Import the DDL script for your database into the database you want to use.

[setting up persistence for business central](https://access.redhat.com/documentation/en-us/red_hat_jboss_bpm_suite/6.4/html/installation_guide/chap_special_setups#setting_up_persistence_for_business_central)

#### Register the data source in Business Central

1) Open EAP_HOME/standalone/deployments/business-central.war/WEB-INF/classes/META-INF/persistence.xml.

2) Locate the `<jta-data-source>` tag and change it to the JNDI name of your data source, for example:

    <jta-data-source>java:/XAOracleDS</jta-data-source>

3) Locate the `<properties>` tag and change the hibernate.dialect property, for example:

    <property name="hibernate.dialect" value="org.hibernate.dialect.DisabledFollowOnLockOracle10gDialect" />
    

#### Register the data source in Dashbuilder

1) Open EAP_HOME/standalone/deployments/dashbuilder.war/WEB-INF/jboss-web.xml.

2) Locate the `<jndi-name>` tag and change it to the JNDI name of your data source, for example:

    <jndi-name>java:/XAOracleDS</jndi-name>


#### Configuring Persistence for the Intelligent Process Server

Open EAP_HOME/standalone/configuration/standalone.xml and locate the <system-properties> tag.

Add the following properties:

org.kie.server.persistence.ds: The JNDI name of your data source.
org.kie.server.persistence.dialect: The hibernate dialect for your database.


    <property name="org.kie.server.persistence.ds" value="java:jboss/datasources/KieServerDS"/>
    <property name="org.kie.server.persistence.dialect" value="org.hibernate.dialect.DisabledFollowOnLockOracle10gDialect">

Other DB dialect in [Other Related Products Information](other.md)

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

TO BE INVESTIGATED

```
<!--cluster jms-->

<pooled-connection-factory name="activemq-ra" transaction="xa" entries="java:/JmsXA java:jboss/DefaultJMSConnectionFactory" connectors="http-connector"/>
                <broadcast-group name="my-broadcast-group" connectors="http-connector" socket-binding="messaging-group"/>
                <discovery-group name="my-discovery-group" refresh-timeout="10000" socket-binding="messaging-group"/>
```

## Offline maven repository

1. Open `settings.xml`

2. Locate the `activeProfiles` section, remove the online profiles and add the offline one

    
        <activeProfiles>  
            <activeProfile>jboss-brms-bpmsuite-repository-64</activeProfile>
            <activeProfile>business-central</activeProfile>
        </activeProfiles>
        
3. Uncomment the following section (in case you haven't add it) and change `<maven-repodir>` with your actual directory where you placed the offline repo:

    
        <profile>
          <id>jboss-brms-bpmsuite-repository-64</id>
          <repositories>
            <repository>
              <id>jboss-brms-bpmsuite-repository-64</id>
              <url>file:///<maven-repodir>/bpm/64/maven-repository</url>
              <releases>
                <enabled>true</enabled>
              </releases>
              <snapshots>
                <enabled>false</enabled>
              </snapshots>
            </repository>
          </repositories>
          <pluginRepositories>
            <pluginRepository>
              <id>jboss-brms-bpmsuite-plugin-repository-64</id>
              <url>file:///<maven-repodir>/bpm/64/maven-repository</url>
              <releases>
                <enabled>true</enabled>
              </releases>
              <snapshots>
                <enabled>false</enabled>
              </snapshots>
            </pluginRepository>
          </pluginRepositories>
        </profile>          
        

    
# Integrating SSO

[SSO Tison article](https://github.com/jboss-gpe-ref-archs/bpms_rhsso/blob/master/doc/bpms_rhsso.adoc)

# System properties

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

### Other info


[Video](https://youtu.be/0UpT92-GIxc) and [configuration](https://github.com/BalakrishnanBalasubramanian/ldap-jbpm-auth)


Problems
============================================================================

## Process Definitions could not be loaded. Check if the jBPM Capabilities are enabled and if the remote server is correctly set up. 

If the Process Server for some misconfiguration (usually on the DBMS) starts without the process capability, the Business Central will store a server configuration without this capability. 
When later, the Process Server configuration is fixed, the Business Central will retain the capabilities defined at the first connection.
To solve this problem:

1. Open Execution Servers page
2. Near the broken server configuration name, **click** the **engine wheel** and then the **Remove** button
3. Restart the kieserver to repeat the registration



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

## Warning "Unable to load key store. Using password from configuration"

You can disregard, it's an advanced configuration:

[https://access.redhat.com/solutions/3669631]()

[https://docs.jboss.org/jbpm/release/7.10.0.Final/jbpm-docs/html_single/#_securing_password_using_key_store]()


## Clone an external repository via SSH protocol

The PAM process needs access to **ssh certificates**:

1. Configure the ssh directory, by default it's in the `<EAP>/bin/.security`, but you can update it with the following system property `org.uberfire.nio.git.ssh.cert.dir`

2. Set the passphrase to read your ssh certificate with the following system property `org.uberfire.nio.git.ssh.passphrase`



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

#### workaround:

Add to ~/.ssh/config the followings:

    Host *
        VerifyHostKeyDNS no
        StrictHostKeyChecking no
        HostKeyAlgorithms +ssh-dss
        UserKnownHostsFile /dev/null


Old ssh does not accept `+`, so change with:

    HostKeyAlgorithms ssh-dss


## inotify watches reached

Exception:

    Caused by: java.io.IOException: User limit of inotify watches reached
    	at sun.nio.fs.LinuxWatchService$Poller.implRegister(LinuxWatchService.java:264)
    
Add the following lines to /etc/sysctl.conf:

    fs.inotify.max_user_watches = 524288
    fs.inotify.max_user_instances = 524288
    
## security strength of SHA-1 digest algorithm 

Exception:

    [org.apache.sshd.server.session.ServerSession] (sshd-SshServer[36dcd1db]-nio2-thread-3) Exception caught: java.security.InvalidKeyException: The security strength of SHA-1 digest algorithm is not sufficient for this key size
    	at sun.security.provider.DSA.checkKey(DSA.java:104)
    	at sun.security.provider.DSA.engineInitSign(DSA.java:136)


Add the following system property:

    <property name="org.uberfire.nio.git.ssh.algorithm" value="RSA"/>
