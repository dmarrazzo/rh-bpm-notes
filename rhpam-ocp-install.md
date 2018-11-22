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
   -p IMAGE_STREAM_NAMESPACE=${OPENSHIFT_IMAGE_STREAM_NAMESPACE} \

## Expose git ssh

	oc expose dc myapp-rhpamcentr --type=LoadBalancer --name=git-ssh

# Troubleshooting

## H2 password

if you change password you have delete previous h2 or rename


## PV

	/mnt/sda1/var/lib/minishift/base/openshift.local.pv/pv0098/.niogit