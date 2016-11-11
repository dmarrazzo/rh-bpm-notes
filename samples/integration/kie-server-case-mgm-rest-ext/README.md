# Kie Server Extension to add Case Management APIs

API Implemented:

	cmService.createDynamicWorkTask(processInstanceId, taskName, taskParams);
	
Requirements: 

- jbpm-case-mgmt-6.4.0.Final-redhat-10.jar

##Build

	mvn package

##Installation

Copy the following files in the kieserver lib dir (`<kie-server-war-path>\WEB-INF\lib`)

- jbpm-case-mgmt-6.4.0.Final-redhat-10.jar
- kie-server-case-mgm-rest-ext-1.0-SNAPSHOT.jar


## Usage

	URL: <hostname>:<port>/kie-server/services/rest/server/containers/<container-name>/processes/instances/<instance-name>/dynamic-workitem/<task-name>
	Method: POST

E.g. payload:
	
	{
	 "Url" : "http://localhost:8090/rest",
	 "Method" : "GET",
	 "Content" : {
	       "payload": {
	            "model.Payload": {
	                   "field1": "f1",
	                   "field2": null,
	                   "field3": null
	            }
	         }
	    }
	}
