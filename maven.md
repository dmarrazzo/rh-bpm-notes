Maven survival guide
====================

Bill of Material:

## Red Hat Process Automation Manager v7.1 Bill of Material (BOM)

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.redhat.ba</groupId>
				<artifactId>ba-platform-bom</artifactId>
				<version>${version}</version>
				<scope>import</scope>
				<type>pom</type>
			</dependency>
		</dependencies>
	</dependencyManagement>

Version property:

	<properties>
		<version>7.1.0.GA-redhat-00003</version>
	</properties>

| Product version | BOM Version        |
|-----------------|--------------------|
| 7.1.0           | 7.1.0.GA-redhat-00003  |

## Red Hat Process Automation Manager v7.0 Bill of Material (BOM)

Bill of Material:

	<dependencyManagement>
		<dependencies>
            <dependency>
              <groupId>org.jboss.bom</groupId>
              <artifactId>rhpam-platform-bom</artifactId>
              <version>${version}</version>
              <scope>import</scope>
              <type>pom</type>
            </dependency>
		</dependencies>
	</dependencyManagement>


Version property:

	<properties>
		<version>7.0.2.GA-redhat-2</version>
	</properties>


| Product version | BOM Version        |
|-----------------|--------------------|
| 7.0.0           | 7.0.0.GA-redhat-3  |
| 7.0.1           | 7.0.1.GA-redhat-1  |
| 7.0.2           | 7.0.2.GA-redhat-2  |

Reference:

[What is the mapping between RHPAM product and maven library version?](https://access.redhat.com/solutions/3405361)

## Red Hat Decision Manager v7.0 Bill of Material (BOM)

Bill of Material

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.jboss.bom.rhdm</groupId>
				<artifactId>rhdm-platform-bom</artifactId>
				<version>${version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

Version property:

	<properties>
		<version>7.0.1.GA-redhat-2</version>
	</properties>


| Product version | BOM Version        |
|-----------------|--------------------|
| 7.0.0           | 7.0.0.GA-redhat-2  |
| 7.0.1           | 7.0.1.GA-redhat-2  |



### Business Optimizer dependencies

	<dependencies>
		<dependency>
			<groupId>org.optaplanner</groupId>
			<artifactId>optaplanner-core</artifactId>
		</dependency>
		<dependency>
			<groupId>org.optaplanner</groupId>
			<artifactId>optaplanner-persistence-common</artifactId>
		</dependency>
		<dependency><!-- examples that use the XStream integration -->
			<groupId>org.optaplanner</groupId>
			<artifactId>optaplanner-persistence-xstream</artifactId>
		</dependency>
		<dependency><!-- examples that use the JAXB integration -->
			<groupId>org.optaplanner</groupId>
			<artifactId>optaplanner-persistence-jaxb</artifactId>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Logging -->
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
		</dependency>
	</dependencies>

## Logging

it is important for all Java plain execution:

		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<scope>provided</scope>
		</dependency>


## Red Hat JBoss BPM Suite v6.4 Bill of material (BOM)

[What is the mapping between BRMS / BPM Suite product and maven library version?](https://access.redhat.com/solutions/2985841)

To simplify the dependency management you can add this:

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.jboss.bom.brms</groupId>
				<artifactId>jboss-brms-bpmsuite-platform-bom</artifactId>
				<version>6.4.7.GA-redhat-1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

Versions:

 - 6.4.6.GA-redhat-1
 - 6.4.5.GA-redhat-3
 - 6.4.4.GA-redhat-3
 - 6.4.3.GA-redhat-2
 - 6.4.2.GA-redhat-2
 - 6.4.1.GA-redhat-3
 - 6.4.0.GA-redhat-2


You don't need to configure the dependency version number, because it's centrally handled by the BOM.


Basic lib:

    	<dependency>
    		<groupId>org.kie</groupId>
    		<artifactId>kie-api</artifactId>
    		<scope>provided</scope>
    	</dependency>
    	<dependency>
    		<groupId>org.drools</groupId>
    		<artifactId>drools-compiler</artifactId>
    		<scope>provided</scope>
    	</dependency>


JPA:

    	<dependency>
    		<groupId>org.jbpm</groupId>
    		<artifactId>jbpm-persistence-jpa</artifactId>
    		<scope>provided</scope>
    	</dependency>

Testing:

    <dependency>
        	<groupId>org.jbpm</groupId>
        	<artifactId>jbpm-test</artifactId>
    </dependency>

Human Task:

    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-human-task-core</artifactId>
    </dependency>

Kie runtime services

    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-kie-services</artifactId>
    </dependency>
    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-services-api</artifactId>
    </dependency>
    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-services-ejb-api</artifactId>
    </dependency>
    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-services-ejb-impl</artifactId>
    </dependency>

Java EE (do not use in bpm project):

    <dependency>
      <groupId>javax</groupId>
      <artifactId>javaee-api</artifactId>
      <version>6.0</version>
    </dependency>


## kie plugin

This is used to build the kjar package

	<build>
		<plugins>
			<plugin>
				<groupId>org.kie</groupId>
				<artifactId>kie-maven-plugin</artifactId>
				<version>6.4.0.Final-redhat-3</version>
				<extensions>true</extensions>
			</plugin>
		</plugins>
	</build>

### Error Missing parameter for pluginExecutionFilter

Eclipse has a problem with the version 7.1 of the plugin:

()[https://issues.jboss.org/browse/DROOLS-3236]

Work around: use a previous release

	<plugin>
		<groupId>org.kie</groupId>
		<artifactId>kie-maven-plugin</artifactId>
		<version>7.9.0.Final</version>
		<extensions>true</extensions>
	</plugin>


## BPM Internal Maven repository

Business Central hosts an internal maven repository.
You can see and upload new artifacts of the Internal repository from the UI:

* Authoring -> Artifact repository

The upload can be automated with following procedure (acknowledgements to Anton Gerli)

- in the project pom.xml add

        <distributionManagement>
            <repository>
                <id>guvnor-m2-repo</id>
                <name>maven repo</name>
                <url>http://10.64.4.78:8080/business-central/maven2/</url>
                <layout>default</layout>
            </repository>
        </distributionManagement>

- Configure server connection in your `~/.m2/settings.xml`

        <server>
            <id>guvnor-m2-repo</id>
            <username>user</username>
            <password>password</password>
            <privateKey>prdprivatekey</privateKey>
            <configuration>
                <wagonProvider>httpclient</wagonProvider>
                <httpConfiguration>
                    <all>
                        <usePreemptive>true</usePreemptive>
                    </all>
                </httpConfiguration>
            </configuration>
        </server>

**References:**

[Uploading Artifacts to Maven Repository](https://access.redhat.com/documentation/en-us/red_hat_jboss_bpm_suite/6.4/html/development_guide/chap_maven_dependencies#uploading_artifacts_to_maven_repository)


### To configure the internal maven directory

System property:

<property name="kie.maven.settings.custom" value="/opt/jboss/.m2/settings.xml"/>

Configure the local repository in settings.xml:

<localRepository>/opt/jboss/.m2/repository</localRepository>


## Compilation setting (build)
The following configuration set the **JDK level** and **exclude** unwanted files

```
  <build>
    [...]
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.7.0</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
          <excludes>
            <exclude>**/.*.java</exclude>
          </excludes>
        </configuration>
      </plugin>
    </plugins>
    [...]
  </build>
```

## Dependency Scope

Dependency scope is used to limit the transitivity of a dependency, and also to affect the classpath used for various build tasks.

There are 6 scopes available:

- **compile**  
This is the default scope, used if none is specified. Compile dependencies are available in all classpaths of a project. Furthermore, those dependencies are propagated to dependent projects.
- **provided**  
This is much like compile, but indicates you expect the JDK or a container to provide the dependency at runtime. For example, when building a web application for the Java Enterprise Edition, you would set the dependency on the Servlet API and related Java EE APIs to scope provided because the web container provides those classes. This scope is only available on the compilation and test classpath, and is not transitive.

## Javadoc
In Eclipse go to Windows-> Preferences-> Maven. Check the box that says "Download Artifact Javadoc."

## Dependency Version

See the [POM Syntax section of the Maven book][1] for more details. Or see this doc on Dependency Version Ranges, where:

A square bracket ( [ & ] ) means "closed" (inclusive).
A parenthesis ( ( & ) ) means "open" (exclusive).

Declare an open-ended version range (will resolve to 2.0.0):

    <version>[1.0.0,)</version>


[1]: http://www.mojohaus.org/versions-maven-plugin/examples/resolve-ranges.html

## Configure Maven Repository

### Off line repositories

Configure the file system

- Download and unzip official maven repo
- Change the maven `setting.xml` to point that

### On line repositories

There are 2 online repositories:

 - [https://maven.repository.redhat.com/ga]()
 - [https://maven.repository.redhat.com/earlyaccess/all/]()

**Further information:** in the chapter *Maven Repository* of the *Installation Guide*.

The following command pull all the dependencies in local repository:

    mvn dependency:go-offline

## How to

### Check artefact existence


    mvn dependency:get -Dartifact=g:a:v

To check a specific repo

    mvn dependency:get -Dartifact=g:a:v -o -DrepoUrl=http://192.168.1.200:8080/business-central/maven2/


### Skip Tests

    mvn install -DskipTests


### Force dependency download

Command line:

    mvn package -U

`-U` means force update of dependencies.

In eclipse, there is a properties in the Run Configuration...

### Problem eclipse cannot find the dependency

Sometime even if the maven builds successfully it raise some warnings about maven resources that has an erroneous header.

E.g.

    [WARNING] error reading /home/donato/.m2/repository/org/jbpm/jbpm-flow/6.3.0.Final-redhat-5/jbpm-flow-6.3.0.Final-redhat-5.jar; invalid LOC header (bad signature)

Eclipse is not able to retrieve the maven dependency, and many compile errors occurs.
The resolution is manually delete the maven dep from .m2 repository and let maven download it again.

### Embed external libraries

 - copy the jar in the resources
 - add dependencies

        <dependency>
            <groupId>example</groupId>
            <artifactId>indennizzo-wsgen</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/src/main/resources/poc.jar</systemPath>
        </dependency>


### Deploy a project from command line (maven)

In your **pom.xml**

    <distributionManagement>
        <repository>
            <id>guvnor-m2-repo</id>
            <name>maven repo</name>
            <url>http://localhost:8080/business-central/maven2/</url>
            <layout>default</layout>
        </repository>
    </distributionManagement>

In your **`~/.m2/settings.xml`**, add this `<server>` element to the `<servers>` section:

    <servers>
    <server>
        <id>guvnor-m2-repo</id>
        <username>bpmsAdmin</username>
        <password>abcd1234!</password>
        <configuration>
            <wagonProvider>httpclient</wagonProvider>
            <httpConfiguration>
                <all>
                    <usePreemptive>true</usePreemptive>
                </all>
            </httpConfiguration>
        </configuration>
    </server>
    </servers>

Now you can deploy with maven command line:

    $ mvn deploy

Here a complete example of maven [settings.xml](config/settings.xml)

### Execute the program

Add to the `pom.xml`

	<build>
		<plugins>

			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>exec-maven-plugin</artifactId>
				<version>1.2.1</version>
				<executions>
					<execution>
						<goals>
							<goal>java</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<mainClass>client.Main</mainClass>
				</configuration>
			</plugin>

		</plugins>
	</build>

then issue the following command:

    mvn -Dkey=value exec:java -Dexec.mainClass=com.yourcompany.yourclass \
        -Dexec.args="arg1 arg2 arg3"

### Config the manifest main class


	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-jar-plugin</artifactId>
		<version>2.6</version>
		<extensions>false</extensions>
		<inherited>true</inherited>
		<configuration>
			<archive>
				<manifest>
					<mainClass>client.Main</mainClass>
				</manifest>
			</archive>
		</configuration>
	</plugin>


# Build a jar with all dependencies

```
<!-- single jar with dep: mvn assembly:assembly -->
<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
    <archive>
      <manifest>
        <mainClass>dm.app.PrevMain</mainClass>
      </manifest>
    </archive>
  </configuration>
</plugin>
```
