# Install RHPAM in OpenShift

## Registry auth

Create credential:

	oc create secret docker-registry red-hat-container-registry --docker-server=https://registry.redhat.io   --docker-username="$REGISTRY_REDHAT_IO_USERNAME"   --docker-password="$REGISTRY_REDHAT_IO_PASSWORD"  --docker-email="$REGISTRY_REDHAT_IO_USERNAME"
	oc secrets link builder red-hat-container-registry --for=pull

Not sure (???):

	oc create secret docker-registry rh-registry --docker-server=registry.redhat.io --docker-username="$REGISTRY_REDHAT_IO_USERNAME" --docker-password="$REGISTRY_REDHAT_IO_PASSWORD" --docker-email="$REGISTRY_REDHAT_IO_USERNAME"

Manually import image:

	oc import-image rhpam71-businesscentral-openshift:1.1
	oc import-image rhpam71-kieserver-openshift:1.1


## Image streams

Login as system admin:

	oc login -u system:admin
    oc project openshift

Create the template:

	oc create -f rhpam71-image-streams.yaml

List template:

	oc get imagestreams.image.openshift.io | grep rhpam71

## Import templates

Optionally, you can import template the templates in order to enrich the catalogue

	cd <template_dir>
	ls *yaml | xargs -n 1 oc create -n openshift -f


## Delete imagestreams

if you need to delete a previous version

	oc delete imagestreams.image.openshift.io/rhpam71-smartrouter-openshift	

delete all imagestream

	oc get imagestreams.image.openshift.io | grep rhpam71 | awk '{print "is/"$1}' |xargs oc delete 

## Login as developer

	oc login -u developer
	oc project myproject

## Create secret

(Generate_a_SSL_Encryption_Key_and_Certificate)[]https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.1/html-single/Security_Guide/index.html#Generate_a_SSL_Encryption_Key_and_Certificate]

	keytool -genkeypair -alias jboss -keyalg RSA -keystore keystore.jks -storepass mykeystorepass --dname "CN=jsmith,OU=Engineering,O=mycompany.com,L=Raleigh,S=NC,C=US"
	oc create secret generic kieserver-app-secret --from-file=keystore.jks
	oc create secret generic businesscentral-app-secret --from-file=keystore.jks	


## Create the app

	oc new-app -f rhpam71-authoring-postgres-custom.yaml \
		-p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
		-p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
		-p KIE_ADMIN_PWD=password \
		-p KIE_SERVER_PWD=password \
		-p KIE_SERVER_CONTROLLER_PWD=password

Other parameters:

	oc new-app -f templates/${OPENSHIFT_TEMPLATE_NAME}.yaml \
	   -p APPLICATION_NAME=${PROJECT_NAME} \
	   -p KIE_ADMIN_USER=${BUSINESS_CENTRAL_USER} \
	   -p KIE_ADMIN_PWD=${BUSINESS_CENTRAL_PASSWORD} \
	   -p KIE_SERVER_H2_USER=${KIE_SERVER_DATABASE_USER} \
	   -p KIE_SERVER_H2_PWD=${KIE_SERVER_DATABASE_PASSWORD} \
	   -p BUSINESS_CENTRAL_MAVEN_USERNAME=${BUSINESS_CENTRAL_MAVEN_USER} \
	   -p BUSINESS_CENTRAL_MAVEN_PASSWORD=${BUSINESS_CENTRAL_MAVEN_PASSWORD} \
	   -p BUSINESS_CENTRAL_HTTPS_PASSWORD=${BUSINESS_CENTRAL_PASSWORD} \
	   -p BUSINESS_CENTRAL_HTTPS_SECRET=businesscentral-app-secret \
	   -p BUSINESS_CENTRAL_HTTPS_NAME=jboss \
	   -p KIE_SERVER_USER=${PROCESS_SERVER_USER} \
	   -p KIE_SERVER_PWD=${PROCESS_SERVER_PASSWORD} \
	   -p KIE_SERVER_CONTROLLER_USER=${PROCESS_SERVER_CONTROLLER_USER} \
	   -p KIE_SERVER_CONTROLLER_PWD=${PROCESS_SERVER_CONTROLLER_PASSWORD} \
	   -p KIE_SERVER_HTTPS_PASSWORD=${PROCESS_SERVER_PASSWORD} \
	   -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret \
	   -p KIE_SERVER_HTTPS_NAME=jboss \
	   -p IMAGE_STREAM_NAMESPACE=${OPENSHIFT_IMAGE_STREAM_NAMESPACE}
   
## PostgreSQL Template


[Template custom for PostgreSQL](config/rhpam71-authoring-postgresql-custom.yaml)
 

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

3) vi rhpam71-trial-ephemeral.yaml (new sections are 'volume' and 'volumeMounts')

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

4) Deploy the app from the modified rhpam71-trial-ephemeral.yaml

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

# OpenShift cheat sheet

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

### delete the application

	oc delete all -l app=rhpam71-authoring

### server log
	
	oc log -f <pod-name>

