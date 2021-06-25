Microcks install:
-----------------

ref: https://microcks.io/documentation/installing/openshift/#openshift-4x


create project

  oc new-project xxxx

Load templates

  oc create -f https://raw.githubusercontent.com/microcks/microcks/master/install/openshift/openshift-ephemeral-full-template-https-ocp4.yml 
  oc create -f https://raw.githubusercontent.com/microcks/microcks/master/install/openshift/openshift-ephemeral-no-keycloak-template-https-ocp4 yml 
  oc create -f https://raw.githubusercontent.com/microcks/microcks/master/install/openshift/openshift-persistent-full-template-https-ocp4.yml 
  oc create -f https://raw.githubusercontent.com/microcks/microcks/master/install/openshift openshift-persistent-no-keycloak-template-https-ocp4.yml 

Deploy

  oc new-app --template=microcks-persistent-https \
  --param=APP_ROUTE_HOSTNAME=microcks-microcks.apps.cluster-1353.1353.example.opentlc.com \
  --param=KEYCLOAK_ROUTE_HOSTNAME=keycloak-microcks.apps.cluster-1353.1353.example.opentlc.com

Create User (admin)

obtain admin password:

  oc get secret microcks-keycloak-admin --template='{{.data.password}}' | base64 -D

 login in Keycloak using admin/PASSWORD

 create a test/test user

 under
  Users->test->RoleMappings->ClientRoles
 type in
  microcks-app

 it's a hidden role, add it, and select admin role to be added



Apicurio install
----------------

deploy Database:

 $ oc create -f https://raw.githubusercontent.com/Apicurio/apicurio-studio/master/distro/openshift/apicurio-postgres-template.yml -n demo-api-lifecycle

 $ oc new-app --template=apicurio-postgres -p GENERATED_DB_USER=apicurio -p GENERATED_DB_PASS=apicurio -p DB_NAME=apicurio -n demo-api-lifecycle

deploy Apicurio

 $ export OPENSHIFT_APPS_URL=apps.$(echo $(oc whoami --show-server) | sed -E -n 's=https://api.(.*):6443=\1=p')

 $ oc create -f https://github.com/Apicurio/apicurio-studio/raw/master/distro/openshift/apicurio-standalone-template.yml -n demo-api-lifecycle
 $ oc new-app --template=apicurio-studio-standalone -p GENERATED_DB_USER=apicurio -p GENERATED_DB_PASS=apicurio -p DB_NAME=apicurio -p GENERATED_KC_USER=dummy -p GENERATED_KC_PASS=dummy -p KC_REALM=apicurio -p AUTH_ROUTE=apicurio-auth.$OPENSHIFT_APPS_URL -p WS_ROUTE=apicurio-studio-ws.$OPENSHIFT_APPS_URL -p API_ROUTE=apicurio-studio-api.$OPENSHIFT_APPS_URL -p UI_ROUTE=apicurio-studio.$OPENSHIFT_APPS_URL -n demo-api-lifecycle

 Obtain the 'Microcks Service Account' secret from Microcks's Keycloack:

  Under
   Clients->microcks-serviceaccount->Credentials->Secret

  copy value and use in command below

Enable Microcks integration

 $ oc set env dc/apicurio-studio-api APICURIO_MICROCKS_API_URL=https://microcks-microcks.$OPENSHIFT_APPS_URL/api APICURIO_MICROCKS_CLIENT_ID=microcks-serviceaccount APICURIO_MICROCKS_CLIENT_SECRET=558ab292-98c9-40b2-8482-26939413719c -n demo-api-lifecycle
 $ oc set env dc/apicurio-studio-ui APICURIO_UI_FEATURE_MICROCKS=true APICURIO_UI_FEATURE_ASYNCAPI=true APICURIO_UI_FEATURE_GRAPHQL=true -n demo-api-lifecycle

Create Apicurio User

 open Apicurio's Keycloack

 follow route:
  apicurio-studio-auth
 credentials:
  dummy/dummy

 create user
  test/test
