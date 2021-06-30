# Deploy RHPAM in OpenShift Container Platform using Operators 

## RHPAM Authoring

**Power Tweak** java options to enable the debug

```yaml
apiVersion: app.kiegroup.org/v2
kind: KieApp
metadata:
  name: rhpam-authoring
  namespace: rhpam-prj
spec:
  environment: rhpam-authoring
  commonConfig:
    adminPassword: changeme
  objects:
    servers:
      - jvm:
          javaOptsAppend: '-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n'
```

## Deploy a standalone Business Central

```yaml
apiVersion: app.kiegroup.org/v2
kind: KieApp
metadata:
  name: rhpam-bc-monitoring
  namespace: rhpam-prj
spec:
  environment: rhpam-trial
  commonConfig:
    adminPassword: changeme
  objects:
    console:
      replicas: 1
      env:
        - name: KIE_SERVER_CONTROLLER_OPENSHIFT_ENABLED
          value: 'false'
        - name: KIE_SERVER_STARTUP_STRATEGY
          value: ControllerBasedStartupStrategy
    servers:
      - database:
          type: h2
        replicas: 0
```


## Change the controller Strategy
To enable controller strategy on a KIE Server, set the `KIE_SERVER_STARTUP_STRATEGY` environment variable to `ControllerBasedStartupStrategy` and the `KIE_SERVER_CONTROLLER_OPENSHIFT_ENABLED` environment variable to `false`.

Change the config map:

    oc edit configmap/kieconfigs-7.9.0

**NOTE**
Do not enable the controller strategy in an environment with a high-availability Business Central. In such environments the controller strategy does not function correctly.


# Running OCP images locally - for testing purposes

**Under test**

### Podman

- pull images

```
podman login -u $REGISTRY_REDHAT_IO_USERNAME -p $REGISTRY_REDHAT_IO_PASSWORD registry.redhat.io
podman pull registry.redhat.io/rhpam-7/rhpam-businesscentral-rhel8:latest
podman pull registry.redhat.io/rhpam-7/rhpam-kieserver-rhel8:latest
```

- run the kieserver

```sh
podman run \
  -it \
  --name rhpam-kieserver \
  --mount type=bind,source=$HOME/.m2/repository,target=/home/jboss/.m2/repository \
  -p 8080:8080 \
  --env MAVEN_LOCAL_REPO=/home/jboss/.m2/repository \
  --env KIE_SERVER_CONTAINER_DEPLOYMENT="process_1.0.0-SNAPSHOT(process)=com.poc-lantik:process:1.0.0-SNAPSHOT" \
  --env KIE_SERVER_USER=appUser \
  --env KIE_SERVER_PWD=changeme \
  --env GC_MAX_METASPACE_SIZE=1024 \
  --env KIE_SERVER_MEMORY_LIMIT=4Gi \
  registry.redhat.io/rhpam-7/rhpam-kieserver-rhel8:latest
```


### docker

```sh
docker run \
  -it \
  --name rhpam-kieserver \
  --mount type=bind,source="$(pwd)"/.m2/repository,target=/home/jboss/.m2/repository \
  -p 8080:8080 \
  --env MAVEN_LOCAL_REPO=/home/jboss/.m2/repository \
  --env KIE_SERVER_CONTAINER_DEPLOYMENT="myproj_1.0.0-SNAPSHOT(myproj)=com.example:myproj:1.0.0-SNAPSHOT" \
  --env KIE_SERVER_USER=appUser \
  --env KIE_SERVER_PWD=changeme \
  --env GC_MAX_METASPACE_SIZE=1024 \
  --env KIE_SERVER_MEMORY_LIMIT=4Gi \
  registry.redhat.io/rhpam-7/rhpam-kieserver-rhel8:7.9.1
```
# Create custom image

if you need to change some of the image file:

```Dockerfile
FROM rhpam-7/rhpam-kieserver-rhel8

USER root

COPY application-users.properties /opt/eap/standalone/configuration/application-users.properties
COPY application-roles.properties /opt/eap/standalone/configuration/application-roles.properties

RUN chown jboss:root ${JBOSS_HOME}/standalone/configuration/application-users.properties
RUN chown jboss:root ${JBOSS_HOME}/standalone/configuration/application-roles.properties


USER 185
```

```sh
docker build -t rhpam-7/rhpam-kieserver-rhel8-custom-props:7.11.0 .
```

# Install RHPAM in OpenShift using Template (legacy)

## Login

Login as system admin:

	oc login -u system:admin
	oc project openshift

## Registry auth

Create credential:

	oc create secret docker-registry red-hat-container-registry --docker-server=https://registry.redhat.io   --docker-username="$REGISTRY_REDHAT_IO_USERNAME"   --docker-password="$REGISTRY_REDHAT_IO_PASSWORD"  --docker-email="$REGISTRY_REDHAT_IO_USERNAME"
	oc secrets link builder red-hat-container-registry --for=pull

#### Alternative credentials

You can also create a service account for Red Hat's registry instead of using your RHN credentials. See	[https://access.redhat.com/terms-based-registry/]()

This is actually the recommended way.  Using your RHN login may or may not work if load is high in RHN however the terms based registry tokens always work.

## Image streams definition

Create the images:

	oc create -f rhpam79-image-streams.yaml

List the images:

	oc get imagestreams.image.openshift.io | grep rhpam79

If old image are already present, update them:

	oc apply -f rhpam79-image-streams.yaml

### Import image

Manually import image:

	oc import-image rhpam-kieserver-rhel8:7.9.0
	oc import-image rhpam-businesscentral-rhel8:7.9.0
	oc import-image rhpam-businesscentral-monitoring-rhel8:7.9.0
	oc import-image rhpam-process-migration-rhel8:7.9.0

## Import templates

Optionally, you can import template the templates in order to enrich the catalogue

	cd <template_dir>
	ls *yaml | xargs -n 1 oc create -n openshift -f


## Delete imagestreams

If you need to delete a previous version

	oc delete imagestreams.image.openshift.io/rhpam79-smartrouter-openshift	

delete all imagestream

	oc get imagestreams.image.openshift.io | grep rhpam79 | awk '{print "is/"$1}' |xargs oc delete 

## Create a project

Login as developer

	oc login -u developer -p dev
	oc new-project pam73

If the project already exists:

	oc project pam73

To delete the project:

	oc delete project pam73

## Create SSL secrets

[Generate_a_SSL_Encryption_Key_and_Certificate](https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.1/html-single/Security_Guide/index.html#Generate_a_SSL_Encryption_Key_and_Certificate)

	keytool -genkeypair -alias jboss -keyalg RSA -keystore keystore.jks -storepass mykeystorepass --dname "CN=dmarrazzo,OU=Sales,O=redhat.com,L=Rome,S=RM,C=Italy"
	oc create secret generic kieserver-app-secret --from-file=keystore.jks
	oc create secret generic businesscentral-app-secret --from-file=keystore.jks	

## Create credential secret

	oc create secret generic rhpam-credentials --from-literal=KIE_ADMIN_USER=pamadmin --from-literal=KIE_ADMIN_PWD=adminPassword

### Optionally import a self signed certificate

	keytool -import -v -trustcacerts -alias ALIAS_NAME -file CERT_FILE \
		-keystore keystore.jks -keypass PASSWORD -storepass PASSWORD

Replace the keystore:

	oc create secret generic kieserver-app-secret --from-file=keystore.jks --dry-run -o yaml | oc replace -f -

## Create the app

### Authoring environment

```bash
oc new-app -f rhpam79-authoring.yaml \
 -p APPLICATION_NAME=pam-dev \
 -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=r3dhat1! \
 -p KIE_SERVER_PWD=r3dhat1! \
 -p KIE_SERVER_CONTROLLER_PWD=r3dhat1!
```

If the image streams are not defined in the openshift namespace, it's possible to override it with this parameter `IMAGE_STREAM_NAMESPACE`.

```bash
oc new-app -f rhpam79-authoring.yaml \
 -p APPLICATION_NAME=pam-dev \
 -p IMAGE_STREAM_NAMESPACE=pam73 \
 -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=J2NDavi0 \
 -p KIE_SERVER_PWD=J2NDavi0 \
 -p KIE_SERVER_CONTROLLER_PWD=J2NDavi0
```

Decision Manager

```bash
oc new-app -f rhdm73-authoring.yaml \
 -p APPLICATION_NAME=dm-dev \
 -p IMAGE_STREAM_NAMESPACE=dm73 \
 -p DECISION_CENTRAL_HTTPS_SECRET=businesscentral-app-secret  \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=J2NDavi0 \
 -p KIE_SERVER_PWD=J2NDavi0 \
 -p KIE_SERVER_CONTROLLER_PWD=J2NDavi0
```

### Authoring environment with postgresql

If you want to use Postgress instead of H2 database, you have to customize the template.
See [Modifying the template](https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.2/html-single/deploying_a_red_hat_process_automation_manager_authoring_environment_on_red_hat_openshift_container_platform/index#environment-authoring-single-modify-proc)

```
oc new-app -f rhpam79-authoring-postgresql.yaml \
 -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=r3dhat1! \
 -p KIE_SERVER_PWD=r3dhat1! \
 -p KIE_SERVER_CONTROLLER_PWD=r3dhat1!
```

### Other Environment variables

```bash
 -p OPENSHIFT_TEMPLATE_NAME=rhpam79-authoring \
 -p PROJECT_NAME=pam79 \
 -p BUSINESS_CENTRAL_USER=pamAdmin \
 -p BUSINESS_CENTRAL_PASSWORD=password \
 -p KIE_SERVER_DATABASE_USER=h2user \
 -p KIE_SERVER_DATABASE_PASSWORD=password \
 -p BUSINESS_CENTRAL_MAVEN_USER=maven \
 -p BUSINESS_CENTRAL_MAVEN_PASSWORD=mavenpassword \
 -p BUSINESS_CENTRAL_HTTPS_PASSWORD=mykeystorepass \
 -p KIE_SERVER_USER=kieserver \
 -p KIE_SERVER_PWD=password \
 -p KIE_SERVER_CONTROLLER_USER=controllerUser \
 -p KIE_SERVER_CONTROLLER_PASSWORD=password \
 -p KIE_SERVER_HTTPS_PASSWORD=mykeystorepass \
 -p IMAGE_STREAM_NAMESPACE=openshift
```

### Immutable kieserver

- User name and password for a Red Hat Process Automation Manager administrative user account.user name and password for a Red Hat Process Automation Manager administrative user account.

```sh
oc new-app -f templates/rhpam79-prod-immutable-kieserver.yaml \
-p APPLICATION_NAME=rhpam
-p KIE_SERVER_CONTAINER_DEPLOYMENT=rhpam-kieserver-library=org.openshift.quickstarts:rhpam-kieserver-library:1.6.0-SNAPSHOT \
-p SOURCE_REPOSITORY_URL=https://github.com/jboss-container-images/rhpam-7-openshift-image.git \
-p SOURCE_REPOSITORY_REF=master \
-p CONTEXT_DIR=quickstarts/library-process/library \
-p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
-p CREDENTIALS_SECRET=rhpam-credentials \
-p IMAGE_STREAM_NAMESPACE=rhpam
```

```sh
oc new-app -f rhpam79-prod-immutable-kieserver.yaml \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=r3dhat1! \
 -p KIE_SERVER_PWD=r3dhat1! \
```


### Source 2 Image deployment


```sh
# create ssh key pair
ssh-keygen -C "openshift-source-builder/repo@gitlab" -f repo-at-gitlab -N ''
oc create secret generic repo-at-gitlab-ssh --from-file=ssh-privatekey=repo-at-gitlab --type=kubernetes.io/ssh-auth
oc secrets link builder repo-at-gitlab-ssh
oc annotate secret/repo-at-gitlab-ssh 'build.openshift.io/source-secret-match-uri-1=ssh://git@gitlab.consulting.redhat.com:2222/poc-unicredit/w-ark-kjar.git'
oc set build-secret --source bc/w-ark-kieserver repo-at-gitlab-ssh
oc new-app -f templates/rhpam79-prod-immutable-kieserver.yaml -p APPLICATION_NAME=w-ark -p KIE_SERVER_CONTAINER_DEPLOYMENT=w-ark=com.pocs:w-ark:1.0.0-SNAPSHOT -p SOURCE_REPOSITORY_URL=ssh://git@gitlab.consulting.redhat.com:2222/poc-unicredit/w-ark-kjar.git -p SOURCE_REPOSITORY_REF=master -p CONTEXT_DIR=/ -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret -p CREDENTIALS_SECRET=rhpam-credentials -p IMAGE_STREAM_NAMESPACE=rhpam
```


### Change readiness probe

In minishift or environment with low resources, it's better to raise the readiness timeout.

## PostgreSQL Template

[Template custom for PostgreSQL](config/rhpam79-authoring-postgresql-custom.yaml)

## Expose a service - create a new route

In order to get an accessible URL:

  oc expose service/openshift-kie-springboot --port=8090

## Expose git ssh

Expose all services:

	oc expose dc myapp-rhpamcentr --type=LoadBalancer --name=rhpamcentr-exp

Delete service:

	oc delete svc/rhpamcentr-exp

Check the `NodePort` for `TargetPort 8001` with following command:

	oc describe svc/rhpamcentr-exp

Example of output:

	TargetPort:               8001/TCP
	NodePort:                 port-4  32618/TCP

You can access to the internal git in this way:

	git clone ssh://pamadmin@$(minishift ip):32618/<project path>

Alternatively, you can forward the pod port:

1. Find the pod name:

		oc get pods

2. Forward the port to your localhost

		oc port-forward myapp-rhpamcentr-5-pfd7l 8001
		

References:

[Exposing Services](https://docs.okd.io/latest/minishift/openshift/exposing-services.html)

## Change maven configuration

1) Create your own copy of settings.xml

2) oc create configmap settings.xml --from-file settings.xml

3) vi rhpam79-trial-ephemeral.yaml (new sections are 'volume' and 'volumeMounts')

```
- kind: DeploymentConfig
  apiVersion: v1
  metadata:
    name: "${APPLICATION_NAME}-kieserver"
    <!-- ... snip ...-->
    template:
      metadata:
        name: "${APPLICATION_NAME}-kieserver"
        labels:
          deploymentConfig: "${APPLICATION_NAME}-kieserver"
          application: "${APPLICATION_NAME}"
          service: "${APPLICATION_NAME}-kieserver"
      spec:
        serviceAccountName: "${APPLICATION_NAME}-rhpamsvc"
        terminationGracePeriodSeconds: 60
        volumes:
          - name: settings-volume
            configMap:
              name: settings.xml
              defaultMode: 420
        containers:
        - name: "${APPLICATION_NAME}-kieserver"
          volumeMounts:
            - name: settings-volume
              mountPath: /home/jboss/.m2/settings.xml
              subPath: settings.xml
          image: "${KIE_SERVER_IMAGE_STREAM_NAME}"
          imagePullPolicy: Always
          <!-- ... snip ...-->
```

4) Deploy the app from the modified rhpam79-trial-ephemeral.yaml

5) Navigate to the running kieserver pod and access the Terminal tab (or use `$ oc rsh <pod name>`)

6) Inspect the `/home/jboss/.m2/settings.xml` file and verify it is your custom one.

## Maven proxy

According to [maven documentation](https://maven.apache.org/guides/mini/guide-proxies.html) add the section `<proxies>` to `${user.home}/.m2/settings.xml`

```
<settings>
  .
  .
  <proxies>
   <proxy>
      <id>example-proxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>proxy.example.com</host>
      <port>8080</port>
      <username>proxyuser</username>
      <password>somepassword</password>
      <nonProxyHosts>www.google.com|*.example.com</nonProxyHosts>
    </proxy>
  </proxies>
  .
  .
</settings>
```

# Source 2 image (S2I)

It's possible to override the default configuration of the image using the `configuration` directory in the source code:

- EAP [standalone.xml](https://github.com/jboss-openshift/cct_module/blob/sprint-23/os-eap-s2i/added/s2i/assemble#L96-L102)
- Maven [setting.xml](https://github.com/jboss-openshift/cct_module/blob/sprint-23/os-eap-s2i/added/s2i/assemble#L26-L30)


# Security

It's possible to add a user on the fly using the BC settings (add the role `user`, `developer`, `process-admin`).
Be aware the change will not survive after the pod restart.
For production environments is to integrate the RH SSO.

# Troubleshooting

## H2 password

if you change password you have delete previous h2 or rename


## Persistent Volume location

	/mnt/sda1/var/lib/minishift/base/openshift.local.pv/pv0098/.niogit

## Get the server config

Retrive the configuration xml:

	oc rsync <pod-name>:/opt/eap/standalone/configuration/standalone-openshift.xml .

# OpenShift cheat sheet

### new project

	oc new-project hello-openshift \
	    --description="This is an example project to demonstrate OpenShift v3" \
	    --display-name="Hello OpenShift"

### pod list

	oc get pods

### restart the server (delete the pod)

In other words, you have to delete the pod, in this way OCP will create and start a new one

	oc delete pod <podname>

### Application exposed URLs

	oc get routes

### environment variables

List all

	oc set env dc/myapp-rhpamcentr --list

### get secret config

	oc get secrets businesscentral-app-secret -o=yaml

### get details in yaml

	oc get bc cakephp-mysql-example -o yaml | less

### Clean up

- delete the application

	oc delete all -l app=rhpam72-authoring

- delete all the project

	oc delete all -l application=pam72

- delete all the old pods

	oc get pods|egrep "Error|Completed" | awk '{ print "pod/"$1 }' | xargs oc delete

### scale up and down

  oc scale dc/pam-dev-rhpamcentr --replicas=0
  oc scale dc/pam-dev-kieserver --replicas=0

### server log
	
	oc log -f <pod-name>

### get a file the container

	oc rsync <existing db container with db archive>:/var/lib/mysql/data/db_archive_dir /tmp/
	
	
### change the probe

	oc set probe dc/pam72-kieserver --readiness --initial-delay-seconds=90 --all deploymentconfig.apps.openshift.io/pam72-kieserver probes updated


### Add extra system properties

Add environment variable:

	JAVA_OPTS_APPEND = "-Dfile.encoding=UTF-8 -Dfile.io.encoding=UTF-8 -Dclient.encoding=UTF-8 -DjavaEncoding=UTF-8 -Dorg.apache.catalina.connector.URI_ENCODING=UTF-8"

When the deployment is handled by the operator, it's possible to leverage the `jvm` section:

```
apiVersion: app.kiegroup.org/v2
kind: KieApp
metadata:
  generation: 2
  name: rhpam-trial
  namespace: demo-pam-operator
  selfLink: /apis/app.kiegroup.org/v2/namespaces/demo-pam-operator/kieapps/rhpam-trial
  uid: 9497c82e-edac-419a-b2f7-a6a92970ebce
spec:
  environment: rhpam-trial
  objects:
    servers:
      - jvm:
          javaOptsAppend: >-
            -Dorg.kie.server.xstream.enabled.packages=org.drools.persistence.jpa.marshaller.*
```

### Raise log level

Procedure to raise the PAM log level for the Web Services handler (ephemeral change):

1. Login in your OCP and get pod name for the kie-server

```bash
oc login -u <user>
oc project <project>
oc get pods
[...]
```

2. Open a shell in the pod:

		oc rsh <kieserver-pod-name>
	
3. Start the EAP command line and issue the commands as in the example:

		sh-4.2$ cd /opt/eap/bin/
		sh-4.2$ ./jboss-cli.sh --connect controller=localhost:9990
		[standalone@localhost:9990 /] /subsystem=logging/logger=org.jbpm.process.workitem.webservice/:add(category=org.jbpm.process.workitem.webservice,level=DEBUG,use-parent-handlers=true)
		[standalone@localhost:9990 /] /subsystem=logging/console-handler=CONSOLE:change-log-level(level=DEBUG)
		{"outcome" => "success"}
		[standalone@localhost:9990 /] quit 

4. Close the remote shell

	sh-4.2$ exit

*From version 7.5* It's possible to add the logging via environment variables: 

LOGGER_CATEGORIES=org.kie:DEBUG, org.drools:DEBUG, org.jbpm:DEBUG

More info: https://github.com/jboss-container-images/jboss-eap-modules/blob/EAP_724_OPENJDK11_CR2/os-eap7-launch/added/launch/configure_logger_category.sh


### force a new deployment

You can start a new deployment process manually using the web console, or from the CLI:

	oc rollout latest dc/<name>

### reset the internal git repo

Log in the pod and issue the following commands:

```sh
rm -rf /opt/eap/standalone/data/kie/.niogit
```

### kill forcefully the pod

	# oc delete pod example-pod-1 -n name --grace-period=0

or 

	# oc delete pod example-pod-1 -n name --grace-period=0 --force

# Fine tunining

## System properties

Add custom system properties

	JAVA_OPTS_APPEND=-Dkubernetes.websocket.timeout=10000

	JAVA_OPTS_APPEND=-XX:MetaspaceSize=512M

## Memory issues

[How to change JVM memory options using Red Hat JBoss EAP image for Openshift](https://access.redhat.com/solutions/2682021)

Environment variables:

	CONTAINER_HEAP_PERCENT = 0.5
	INITIAL_HEAP_PERCENT = 0.5
	
Metaspace (works out of S2I?): 

	GC_MAX_METASPACE_SIZE = 512

## timeout

	kubernetes.websocket.timeout

## Grant permission to use operators

oc adm policy add-cluster-role-to-user cluster-reader developer

## Docker image hacking

docker run -ti quay.io/rhpam_rhdm/rhpam-businesscentral-rhel8-cm-showcase:7.5.0 /bin/sleep infinity

config directory: `/opt/eap/bin/launch`


## ConfigMap for properties

	oc create configmap const-props --from-file=use-case-3/const.propertie
	oc set volume dc/rhpam-authoring-kieserver --add --name=config-volume --type=configmap --configmap-name=const-props --mount-path=/etc/config

## Investigating pod issues

https://docs.openshift.com/container-platform/4.5/support/troubleshooting/investigating-pod-issues.html

## ConfigMap for Business Calendar

	oc create configmap jbpm-business-calendar-props --from-file=jbpm.business.calendar.properties

Then added it the Business Central Deployment Configuration:

	oc set volume dc/rhpam-trial-rhpamcentr --add --name=jbpm-business-calendar-volume --type=configmap --configmap-name=jbpm-business-calendar-props --mount-path=/deployments/ROOT.war/WEB-INF/classes


## replace a file 

Use the subpath and the full path to the file

```yaml
	volumeMounts:
      - name: log4j-properties-volume
        mountPath: /zeppelin/conf/log4j.properties
        subPath: log4j.properties

	volumeMounts:
      - name: log4j-properties-volume
        mountPath: /zeppelin/conf
```
## intenal hostname

internal namespace convention:

	my-svc.my-namespace.svc.cluster.local


## ControllerBasedStartupStrategy

[Related issue](https://issues.redhat.com/browse/RHDM-1151)

Procedure for setting KieServer to use ControllerBasedStartupStrategy and connect to an OpenShift enhancement DISABLED Business Central

Step #1: Set ‘false’ to this env variable at Business Central DC

```yaml
- name: KIE_WORKBENCH_CONTROLLER_OPENSHIFT_ENABLED
	value: "true"
```

Step #2: Set ‘ControllerBasedStartupStrategy’ to this env variable at Kie Server DC

```yaml
- name: KIE_SERVER_STARTUP_STRATEGY
	value: "OpenShiftStartupStrategy"
```

Step #3: Add back the following env variables to Kie Server DC

```yaml
- name: KIE_SERVER_CONTROLLER_USER
	value: "${KIE_SERVER_CONTROLLER_USER}"
- name: KIE_SERVER_CONTROLLER_PWD
	value: "${KIE_SERVER_CONTROLLER_PWD}"
- name: KIE_SERVER_CONTROLLER_TOKEN
	value: "${KIE_SERVER_CONTROLLER_TOKEN}"
- name: KIE_SERVER_CONTROLLER_SERVICE
	value: "${APPLICATION_NAME}-rhpamcentr"
- name: KIE_SERVER_CONTROLLER_PROTOCOL
	value: "ws"
```

Step #4 (Optional): To bypass TLS related configurations, Kie Server instance can register itself with Controller using regular Http port with following setting.

```yaml
- name: KIE_SERVER_ROUTE_NAME
	value: "insecure-${APPLICATION_NAME}-kieserver"
```
### How to set up the webhook

From the following command extract the URL:

    oc describe bc/hello-kieserver

E.g. `https://api.shared-na4.na4.openshift.opentlc.com:6443/apis/build.openshift.io/v1/namespaces/rhpam/buildconfigs/hello-kieserver/webhooks/<secret>/github`

Replace `<secret>` with the outcome of the following command:

    oc get bc/hello-kieserver -o yaml | grep -B 2 secret

E.g.

```
  triggers:
  - github:
      secret: 1VajKlz8oR8LLa94
    type: GitHub
  - generic:
      secret: gxqNhyT3
```

In Github:

- open **project > settings**
- select **Webhook** from left side menu
- click **Add webhook** button
- fill in the **URL** accordingly the previous outcomes
- set **content type** to `application/json`

## Openshift Useful links

- [https://docs.openshift.com/container-platform/3.9/dev_guide/copy_files_to_container.html]()

- [https://www.mankier.com/package/origin-clients]()

- [Learn OpenShift interactively](https://learn.openshift.com/)
