# Kie Server 

# Configuration

**controller** is the BC
Each kie server can managed by more than a controller.
The kie server (aka Intelligent Process Server) initiate the conversation with the controller that enlist it in its configuration.

## Adding users

Change into CONTROLLER_HOME/bin.

    ./add-user.sh -a --user kieserver --password kieserver1! --role kie-server

## Bootstrap Switches

### kie-server configuration 

    <property name="org.kie.server.id" value="default-kieserver"/>
    <property name="org.kie.server.repo" value="${jboss.server.data.dir}"/>

The kie server initiate the conversation with the controller that enlist it in its configuration. The following URL is passed back to the controller:

    <property name="org.kie.server.location" value="http://localhost:8080/kie-server/services/rest/server"/>
    
A user name used to connect to the controller REST API

    <property name="org.kie.server.controller.user" value="..."/>
    <property name="org.kie.server.controller.pwd" value="..."/>

Each kie server can managed by more than a controller. The following properties is a comma-separated list of URLs to controller REST endpoints

    <property name="org.kie.server.controller" value="http://localhost:8080/business-central/rest/controller"/>

Other configurations:

    <property name="org.kie.server.persistence.dialect" value="org.hibernate.dialect.H2Dialect"/>
    <property name="org.kie.executor.jms.queue" value="queue/KIE.SERVER.EXECUTOR"/>
    <property name="org.kie.server.persistence.ds" value="java:jboss/datasources/ExampleDS"/>


### controller side configuration

The controller use the same credentials for all the kie-servers, they are defined by the following properties:

    <property name="org.kie.server.user" value="donato"/>
    <property name="org.kie.server.pwd" value="donato"/>

## Execute multiple instances of kie server

- copy the standalone folder
- run the the kieserver

Node 1:
    export JBOSS_HOME=/home/donato/bin/EAP-7
    ./standalone.sh -Djboss.node.name=kie-node1 -Djboss.server.base.dir=$JBOSS_HOME/kie-node1 -c standalone.xml -Djboss.socket.binding.port-offset=1


Node 2:

    export JBOSS_HOME=/home/donato/bin/EAP-7
    ./standalone.sh -Djboss.node.name=kie-node2 -Djboss.server.base.dir=$JBOSS_HOME/kie-node2 -c standalone.xml -Djboss.socket.binding.port-offset=2

# Kie Server REST API

## docs
http://localhost:8080/kie-server/docs/

## List of deployed processes

    GET
    <hostname>:<port>/kie-server/services/rest/server/queries/processes/definitions

Example result:

    {
        "processes": [
            {
                "process-id": "job-redo.RedoProc",
                "process-name": "RedoProc",
                "process-version": "1.0",
                "package": "org.jbpm",
                "container-id": "redo"
            },
        ]
    }

## Create a process instance:

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/<process-id>/instances

## List the process instances

    GET
    <hostname>:<port>/kie-server/services/rest/server/queries/processes/instances


## Delete a process instance

    DELETE
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/instances/<process-instance-id>

## Send a signal

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/instances/signal/<signal-ref>

Sample payload:

    { "test" : {"java.lang.String" : "ok"}}

## List available signal for an instance

    GET
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/instances/<process-instance-id>/signals

## Get the variables

    GET
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/instances/<process-instance-id>/variables

## Set the variables

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/<container-id>/processes/instances/<process-instance-id>/variables

## Container ALIAS

IPS has an alias feature
[https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_jboss_bpm_suite_intelligent_process_server_for_openshift/#Alias-Redirection]()

Ally>

For new processes your REST call might look something like below, which you'd then rewrite at the routing level: 
http://localhost:8080/kie-server/services/rest/server/containers/{container-name}/LATEST

if the container-name has a version then the routing layer acts as a pass-through. 

## Retrieve all the facts

http://stackoverflow.com/questions/34339791/kie-execution-server-guided-rule-inserted-fact-how-to-get-it-in-java
solo XSTREAM !!!

	commands.add(cmdFactory.newGetObjects("objects"));


# KieServer Extensions

Extending REST interface

[http://mswiderski.blogspot.it/2015/12/kie-server-extend-existing-server.html]()

## jBPM Services

object	KModuleDeploymentService  (id=648)	
object	BPMN2DataServiceImpl  (id=655)	
object	ProcessServiceImpl  (id=14234)	

org.jbpm.services.api.ProcessService
- startProcess(String, String)


object	UserTaskServiceImpl  (id=14237)	

object	RuntimeDataServiceImpl  (id=14187)	
org.jbpm.services.api.RuntimeDataService
This service provides an interface to retrieve data about the runtime, including the following:
 * <ul>
 * 	<li>process instances</li>
 * 	<li>process definitions</li>
 * 	<li>node instance information</li>
 * 	<li>variable information</li>
 * </ul>



object	ExecutorServiceImpl  (id=14171)	
Entry point of the executor component. Application should always talk
via this service to ensure all internals are properly initialized

object	FormManagerServiceImpl  (id=14180)	
object	QueryServiceImpl  (id=14254)	
object	KieServerRegistryImpl  (id=14262)	

<map>
  <entry>
    <string>Method</string>
    <string>PUT</string>
  </entry>
  <entry>
    <string>Url</string>
    <string>http://localhost:8090/rest</string>
  </entry>
  <entry>
    <string>Content-Type</string>
    <string>application/json</string>
  </entry>

  <entry>
    <string>Content</string>
<model.Payload>
<field1>aaa</field1> <field2>bb3</field2>  <field3>ccc</field3>
</model.Payload>
  </entry>
</map>

{
  "Url" : "http://localhost:8090/rest",
  "Method" : "GET",
  "Content" : { 
    "field1" : "aaa",
    "field2" : "bbb",
    "field3" : "ccc"
  }
}

## Kie Server image

- In the Business Central enable the SVG saving
- the kjar has to define a kie base "defaultKieBase"
- if the process definition is not in the root folder, the property package of the Process must reflect the actual process location



## Kie Server API

Important articles:

http://mswiderski.blogspot.it/2015/09/unified-kie-execution-server-part-1.html
http://mswiderski.blogspot.it/2015/09/unified-kie-execution-server-part-2.html
http://mswiderski.blogspot.it/2015/09/unified-kie-execution-server-part-3.html
http://mswiderski.blogspot.it/2015/09/unified-kie-execution-server-part-4.html

Initial variables:


        private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
        private static final String user = "";
        private static final String password = "";
        private static final String CONTAINER = "atti";
        

List tasks of a process instance:

        
        	KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, user, password);
        	
        	// Marshalling configuration
        	config.setMarshallingFormat(MarshallingFormat.JSON);
		extraClasses.add(Transaction.class);
		config.addExtraClasses(extraClasses);
        	KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        // client facade
        	UserTaskServicesClient taskServicesClient = client.getServicesClient(UserTaskServicesClient.class);
        	List<String> status = new ArrayList<String>();
        	status.add(Status.Ready.toString());
        	List<TaskSummary> tasksSummaries = taskServicesClient.findTasksAssignedAsPotentialOwner("donato", status, 0, 10);
        	tasksSummaries.forEach((ts)->System.out.println(ts.getDescription()));
    





