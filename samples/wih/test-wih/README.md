# TestWorkitem Handler

Basic WorkItemHandler to test exception handling.
After 4 calls, it generate Exception.
It can be used as a template for WIH.

## To install the WIH

1. Set up your local maven setting to point to guvnor (the BPM maven repository) 
2. Issue `maven deploy`
3. From **Project Editor**:

    - select **Dependenties**
    - click **Add from repository**
    - choose this maven project
     
4. From **Project Editor** open **Deployment Descriptor** and add the new WIH 

    - `TestWIH`
    - `new com.redhat.ssa.bpm.wih.TestWorkItemHandler()`

5. Open Work Item Definitions file and replace the last square bracket with the following 

	  ,
	  [
	    "name" : "TestWIH",
	    "displayName" : "TestWIH",
	  ]
	]

 