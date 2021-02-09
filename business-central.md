# Business Central

**Business Central** is the web console where the user can develop the BPM / rule solution. In the Decision Manager context, it's called **Decision Central**.

In the upstream project, it is called even **workbench** or **jbpm console**.

## Spaces (version 7)

Decision Manager Version 7 introduced the concept of Spaces as a way to group projects.
Each space is mapped in an internal git repository, the name of which is `<spacename>-myrepo`. 
The suffix `myrepo` can be changed in the general settings.

The default space is just "myrepo".

To clone the repository you can use the following command:

```
git clone ssh://<user>@localhost:8001/<space-name>-myrepo
```

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

# Task does not allow multiple incoming sequence flow (uncontrolled flow)

According to BPMN 2.0 specification allows on page 153 multiple incoming flow sequences for activities (i.e. also for tasks). jBPM fails to validate such an BPMN file. It throws an IllegalArgumentException with the message This type of node cannot have more than one incoming connection!.

Multiple incoming and outgoing sequence flows will be accepted in the jBPM Web Designer and by the jBPM6 Engine after adding the system property `jbpm.enable.multi.con=true` while starting BPMS/BRMS server.

[https://access.redhat.com/solutions/779893]()

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

## User Group Management


### Pluggable implementation

controller like this:

https://github.com/kiegroup/kie-wb-distributions/blob/master/business-central-parent/business-central-webapp/src/main/resources/security-management.properties#L17

There are various implementations out-of-the-box, our recommendation in general is to use keycloak where possible 

Default implementation:
https://github.com/kiegroup/appformer/tree/master/uberfire-extensions/uberfire-security/uberfire-security-management


## REST Services

### New in version 7

REST APIs in Red Hat Decision Manager are reorganized to follow the new complete separation of duties:

- Decision Central is responsible for assets management a development. APIs are reachable at the address: `http://<hostname>:8080/decision-central/rest/`  APIs are documented at: [https://docs.jboss.org/jbpm/release/7.6.0.Final/jbpm-docs/html_single/#_drools.workbenchremoteapi]()

- Decision Server (aka kieserver) is responsible for the runtime / deployment. APIs are reachable at the address `http://<hostname>:8080/kie-server/services/rest` There is a swagger documentation at `http://<hostname>:8080/kie-server/docs`

