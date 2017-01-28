# Kie Server config

**controller** is the BC

## Bootstrap Switches


### kie-server configuration

    <property name="org.kie.server.id" value="default-kieserver"/>
    <property name="org.kie.server.repo" value="${jboss.server.data.dir}"/>
    
A user name used to connect to the controller REST API

    <property name="org.kie.server.controller.user" value="..."/>
    <property name="org.kie.server.controller.pwd" value="..."/>

a comma-separated list of URLs to controller REST endpoints

    <property name="org.kie.server.controller" value="http://localhost:8080/business-central/rest/controller"/>

URL of Intelligent Process Server instance used by the controller to call back on this 

    <property name="org.kie.server.location" value="http://localhost:8080/kie-server/services/rest/server"/>


Others:

    <property name="org.kie.server.persistence.dialect" value="org.hibernate.dialect.H2Dialect"/>
    <property name="org.kie.executor.jms.queue" value="queue/KIE.SERVER.EXECUTOR"/>
    <property name="org.kie.server.persistence.ds" value="java:jboss/datasources/ExampleDS"/>


### controller side configuration

a user name used to connect with the KIE server:

    <property name="org.kie.server.controller.user" value="..."/>
    <property name="org.kie.server.controller.pwd" value="..."/>


# Kie Server REST API

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

# KieServer Extensions


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

- enable the SVG saving
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
        	
        	// Marshalling
        	config.setMarshallingFormat(MarshallingFormat.JSON);
        	
        	KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        	
        	UserTaskServicesClient taskServicesClient = client.getServicesClient(UserTaskServicesClient.class);
        	List<String> status = new ArrayList<String>();
        	status.add(Status.Ready.toString());
        	List<TaskSummary> tasksSummaries = taskServicesClient.findTasksAssignedAsPotentialOwner("donato", status, 0, 10);
        	tasksSummaries.forEach((ts)->System.out.println(ts.getDescription()));
    





