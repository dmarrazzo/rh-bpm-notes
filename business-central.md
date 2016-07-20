# Project setup
During development phase, your version SHOULD keep the SNAPSHOT extension, when you need to freeze the code before a release you can remove it, export the code and move your development version to next SNAPSHOT version. In a normal lifecycle, the development environment is setup always on version x.x-SNAPSHOT. SNAPSHOT naming convention has the following benefits on the build process....

- Add **SNAPSHOT** to artifact version. E.g. 1.0-SNAPSHOT

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
