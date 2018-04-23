# Case Management

In order to design a case you have to set **AdHoc** property to **true**.

To design adhoc activities you have to place them in an **adhoc subprocess**.

![Adhoc subprocess](imgs/adhoc.png)

## Adhoc subprocess
An Ad-Hoc Sub-Process is a specialized type of Sub-Process that is a group of Activities that have no REQUIRED sequence relationships.

Properties:

- AdHocCompletionCondition

    the condition that once met the execution is considered successful and finishes

- AdHocCancelRemainingInstances

    if set to true, once the AdHocCompletionCondition is met, execution of any Elements is immediately cancelled.

In the designer the user has possibility to set 'AdHocOrdering' property for Ad Hoc Sub-Processes: parallel and sequential.
Nevertheless, core engine only supports parallel execution, property should be hidden in designer.

## Dependencies

DO NOT import jbpm-case-mgmt in the pom but copy it in WEB-INF/lib!

## Coding

Reference

[CaseMgmtService](https://github.com/kiegroup/jbpm/blob/6.5.x/jbpm-case-mgmt/src/main/java/org/jbpm/casemgmt/CaseMgmtService.java)

### Trigger adhoc fragment

			CaseMgmtService caseMgmtService = new CaseMgmtUtil(engine);
			String[] adHocFragmentNames = caseMgmtService.getAdHocFragmentNames(646);
		
			for (String frag : adHocFragmentNames) {
				System.out.println(">>> "+frag);
			}
			caseMgmtService.triggerAdHocFragment(646, "Segreteria Prepara Pubblicazione");


### Start a dynamic task
org.jbpm.casemgmt.CaseMgmtService cmService = new org.jbpm.casemgmt.CaseMgmtUtil(kcontext);

java.util.Map<String, Object> workParams = new java.util.HashMap<String, Object>();

workParams.put("Message", testVar);


cmService.createDynamicWorkTask(kcontext.getProcessInstance().getId(), "Log", workParams);

# Case management in version 7

Article that describe the new capabilities:
- [Case management - jBPM v7 - Part 1](http://mswiderski.blogspot.it/2016/10/case-management-jbpm-v7-part-1.html)
- [Case management - jBPM v7 - Part 2 - working with case data](http://mswiderski.blogspot.it/2016/10/case-management-jbpm-v7-part-2-working.html)
- [Case management - jBPM v7 - Part 3 - dynamic activities](http://mswiderski.blogspot.it/2016/10/case-management-jbpm-v7-part-3-dynamic.html)
- [Order IT hardware - jBPM 7 case application](http://mswiderski.blogspot.it/2017/01/order-it-hardware-jbpm-7-case.html)
- [case management security](http://mswiderski.blogspot.it/2017/02/jbpm-7-case-management-security.html)
- [Make use of rules to drive your cases](http://mswiderski.blogspot.it/2017/07/make-use-of-rules-to-drive-your-cases.html)
Case management improvements - data authorisation
Case management - mention someone in comments and …
Track your processes and activities with SLA
React to SLA violations in cases


## Project 

- set runtime strategy to Per Case

        <runtime-strategy>PER_CASE</runtime-strategy>

- configure marshallers for case file and documents

        <marshalling-strategies>
            <marshalling-strategy>
                <resolver>mvel</resolver>
                <identifier>org.jbpm.casemgmt.impl.marshalling.CaseMarshallerFactory.builder().withDoc().get();</identifier>
                <parameters/>
            </marshalling-strategy>
            <marshalling-strategy>
                <resolver>mvel</resolver>
                <identifier>new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy("com.example:SimpleCase:1.0", classLoader)</identifier>
                <parameters/>
            </marshalling-strategy>
            <marshalling-strategy>
                <resolver>mvel</resolver>
                <identifier>new org.jbpm.document.marshalling.DocumentMarshallingStrategy();</identifier>
                <parameters/>
            </marshalling-strategy>
        </marshalling-strategies>

- create WIH definitions:

    In the deployment descriptor:

        <work-item-handlers>
            <work-item-handler>
                <resolver>mvel</resolver>
                <identifier>new org.jbpm.casemgmt.impl.wih.StartCaseWorkItemHandler(ksession);</identifier>
                <parameters/>
                <name>StartCaseInstance</name>
            </work-item-handler>
        </work-item-handlers>

    WorkDefinition.wid files in the project and its packages to ensure case related nodes (e.g. Milestone) are available in palette. Example of wid file:


          [
            "name" : "Milestone",
            "parameters" : [
              "Condition" : new StringDataType()
            ],
            "displayName" : "Milestone",
            "icon" : "defaultmilestoneicon.png",
            "category" : "Milestone"
          ],
          
          [
            "name" : "StartCaseInstance",
            "parameters" : [
              "DeploymentId" : new StringDataType(),
              "CaseDefinitionId" : new StringDataType(),
              "Data_" : new StringDataType(),
              "UserRole_" : new StringDataType(),
              "GroupRole_" : new StringDataType(),
              "DataAccess_" : new StringDataType(),
              "Independent" : new StringDataType(),
              "DestroyOnAbort" : new StringDataType()
            ],
            "results" : [
                "CaseId" : new StringDataType(),
            ],
            "displayName" : "Sub Case",
            "icon" : "defaultsubcaseicon.png",
            "category" : "Cases"
          ]





## Process 

- adhoc true
- case id prefix


## Milestone

Milestones are triggered when a case file variable reach a specific state.
The variable MUST be declared case file, flag it in the process variable list.

**Condition** requires a Drool expression like in the following example:

    org.kie.api.runtime.process.CaseData(data.get("ok") == true)


## Roles

Roles simplify the dynamic role assignment, they work like variables, used at task creation time to identify the right user / group.


## Case Management Security

By default case instance security is enabled. It does protect each case instance from being seen by users who do not belong to a case in anyway. In other words, if you are not part of case role assignment (either assigned as user or a group member) then you won't be able to get access to the case instance.


Authorisation can also be turned off by system property: 

- `org.jbpm.cases.auth.enabled` when set to false.

Above access is just one part of the security for case instances. In addition, there is case instance operations that can be restricted to case roles. Here is the list of currently supported case instance operations that can be configured:

- CANCEL_CASE
- DESTROY_CASE
- REOPEN_CASE
- ADD_TASK_TO_CASE
- ADD_PROCESS_TO_CASE
- ADD_DATA
- REMOVE_DATA
- MODIFY_ROLE_ASSIGNMENT
- MODIFY_COMMENT

by default three of these operations:

- CANCEL_CASE
- DESTROY_CASE
- REOPEN_CASE

are protected with following roles:

- owner
- admin

again, these are case roles so based on case role assignments can differ between case instances. Thus allowing maximum flexibility in how to utilise them.

security for case operations is configurable via simple property file called `case-authorization.properties` that should be available at root of the class path upon start of the case application. Format of this file is extremely simple:

OPERATION=role1,role2,roleN

## Case service

Use the case APIs:

    org.jbpm.casemgmt.api.CaseService caseService =  org.jbpm.services.api.service.ServiceRegistry.get().service(org.jbpm.services.api.service.ServiceRegistry.CASE_SERVICE);
    caseService.triggerAdHocFragment(...);


## Case Rules

    rule "ask user for details"
    
    when 
        $caseData : CaseFileInstance()
        String(this == "AskForDetails") from $caseData.getData("decision")
              
    then 
        $caseData.remove("decision");
        CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reason", "How did it happen?");
        caseService.addDynamicTask($caseData.getCaseId(), caseService.newHumanTaskSpec("Please provide additional details", "Action", "insured", null, parameters));
        
    end






