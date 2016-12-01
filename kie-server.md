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







