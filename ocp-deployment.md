# Install RHPAM in OpenShift

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

	oc create -f rhpam73-image-streams.yaml

List the images:

	oc get imagestreams.image.openshift.io | grep rhpam73

### Import image

Manually import image:

	oc import-image rhpam73-businesscentral-openshift:1.0
	oc import-image rhpam73-kieserver-openshift:1.0

## Import templates

Optionally, you can import template the templates in order to enrich the catalogue

	cd <template_dir>
	ls *yaml | xargs -n 1 oc create -n openshift -f


## Delete imagestreams

If you need to delete a previous version

	oc delete imagestreams.image.openshift.io/rhpam72-smartrouter-openshift	

delete all imagestream

	oc get imagestreams.image.openshift.io | grep rhpam72 | awk '{print "is/"$1}' |xargs oc delete 

## Create a project

Login as developer

	oc login -u developer -p dev
	oc new-project pam73

If the project already exists:

	oc project pam73

To delete the project:

	oc delete project pam73

## Create secret

[Generate_a_SSL_Encryption_Key_and_Certificate](https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.1/html-single/Security_Guide/index.html#Generate_a_SSL_Encryption_Key_and_Certificate)

	keytool -genkeypair -alias jboss -keyalg RSA -keystore keystore.jks -storepass mykeystorepass --dname "CN=jsmith,OU=Engineering,O=mycompany.com,L=Raleigh,S=NC,C=US"
	oc create secret generic kieserver-app-secret --from-file=keystore.jks
	oc create secret generic businesscentral-app-secret --from-file=keystore.jks	

### Optionally import a self signed certificate

	keytool -import -v -trustcacerts -alias ALIAS_NAME -file CERT_FILE \
		-keystore keystore.jks -keypass PASSWORD -storepass PASSWORD

Replace the keystore:

	oc create secret generic kieserver-app-secret --from-file=keystore.jks --dry-run -o yaml | oc replace -f -

## Create the app

### Authoring environment

```bash
oc new-app -f rhpam73-authoring.yaml \
 -p APPLICATION_NAME=pam-dev \
 -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=r3dhat1! \
 -p KIE_SERVER_PWD=r3dhat1! \
 -p KIE_SERVER_CONTROLLER_PWD=r3dhat1!
```

If the image streams are not defined in the openshift namespace, it's possible to override it with this parameter `IMAGE_STREAM_NAMESPACE`.

```bash
oc new-app -f rhpam73-authoring.yaml \
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
oc new-app -f rhpam72-authoring-postgresql.yaml \
 -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
 -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
 -p KIE_ADMIN_PWD=r3dhat1! \
 -p KIE_SERVER_PWD=r3dhat1! \
 -p KIE_SERVER_CONTROLLER_PWD=r3dhat1!
```

### Other Environment variables

```bash
 -p OPENSHIFT_TEMPLATE_NAME=rhpam72-authoring \
 -p PROJECT_NAME=pam72 \
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

 
### Change readiness probe

In minishift or environment with low resources, it's better to raise the readiness timeout.

## PostgreSQL Template

[Template custom for PostgreSQL](config/rhpam72-authoring-postgresql-custom.yaml)

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

	git clone ssh://adminUser@$(minishift ip):32618/<project path>

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

3) vi rhpam72-trial-ephemeral.yaml (new sections are 'volume' and 'volumeMounts')

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

4) Deploy the app from the modified rhpam72-trial-ephemeral.yaml

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

### delete the application

	oc delete all -l app=rhpam72-authoring

### delete all the project

	oc delete all -l application=pam72


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

Environment variables:

	CONTAINER_HEAP_PERCENT = 0.5
	INITIAL_HEAP_PERCENT = 0.5
	
Metaspace (works out of S2I?): 

	GC_MAX_METASPACE_SIZE = 512

## timeout

	kubernetes.websocket.timeout

## Openshift Useful links

- [https://docs.openshift.com/container-platform/3.9/dev_guide/copy_files_to_container.html]()

- [https://www.mankier.com/package/origin-clients]()