# Maven survival guide

##Setting JDK level

      <build>
        [...]
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
              <source>1.7</source>
              <target>1.7</target>
            </configuration>
          </plugin>
        </plugins>
        [...]
      </build>

##BPM libraries


	<properties>
		<version.bpms>6.4.0.Final-redhat-3</version.bpms>
	</properties>
	<dependencies>

		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-api</artifactId>
			<version>${version.bpms}</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-compiler</artifactId>
			<version>${version.bpms}</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.jbpm</groupId>
			<artifactId>jbpm-test</artifactId>
			<version>${version.bpms}</version>
		</dependency>
	</dependencies>



## Dependency Scope

Dependency scope is used to limit the transitivity of a dependency, and also to affect the classpath used for various build tasks.

There are 6 scopes available:

- **compile**  
This is the default scope, used if none is specified. Compile dependencies are available in all classpaths of a project. Furthermore, those dependencies are propagated to dependent projects.
- **provided**  
This is much like compile, but indicates you expect the JDK or a container to provide the dependency at runtime. For example, when building a web application for the Java Enterprise Edition, you would set the dependency on the Servlet API and related Java EE APIs to scope provided because the web container provides those classes. This scope is only available on the compilation and test classpath, and is not transitive.

## How to

### Force dependency download

Command line:

    mvn package -U

`-U` means force update of dependencies.

In eclipse, there is a properties in the Run Configuration...


