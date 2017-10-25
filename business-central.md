# Business Central

## Artefacts

The BC relies on 3 files system:

- git internal repository, which directory is `.niogit`
- maven internal deployment `repositories`
- artefact index, which directory is `.index`

In order to get a fresh BC environment, delete the previous directories.

[An unmanaged repository unexpectedly turned into "managed" in BRMS / BPM Suite](https://access.redhat.com/solutions/2999391)

## Project setup
During development phase, your version SHOULD keep the SNAPSHOT extension, when you need to freeze the code before a release you can remove it, export the code and move your development version to next SNAPSHOT version. In a normal lifecycle, the development environment is setup always on version x.x-SNAPSHOT. SNAPSHOT naming convention has the following benefits on the build process....

- Add **SNAPSHOT** to artifact version. E.g. 1.0-SNAPSHOT

## Workbench Configuration

Within Red Hat JBoss BPM Suite, users may set up roles using LDAP to modify existing roles. Users may modify the roles in the workbench configuration to ensure the unique LDAP based roles conform to enterprise standards by editing the deployments directory located at JBOSS_HOME/standalone/deployments/business-central.war/WEB-INF/classes/workbench-policy.propeties.

Example of customization:

    profile.wb_for_business_analysts=!wb_artifact_repository, !wb_artifact_repository_jar_download, wb_authoring, !wb_data_modeler_edit_sources

## Internal Maven repository set up

remove from web.xml access restrictions


Alternative approach:
Retrieving dependency files from http://localhost:8080/business-central/maven2/ will requires authentication.

You need to add following configuration in the ~/m2/setting.xml

    <servers>
        <server>
            <id>brms-m2-repo</id>
            <username>bpmsAdmin</username>
            <password>P@ssw0rd</password>
            <configuration>
                <wagonprovider>httpclient</wagonprovider>
                <httpconfiguration>
                    <all>
                        <usepreemptive>true</usepreemptive>
                    </all>
                </httpconfiguration>
            </configuration>
        </server>
    </servers>

Make sure this following Maven repo are also in the setting.xml

## REST Services


### remote API

If you are creating a data object, make sure that the class has the `@org.kie.api.remote.Remotable` annotation. The `@org.kie.api.remote.Remotable` annotation makes the entity available for use with JBoss BPM Suite remote services such as REST, JMS, and WS.
