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

Exclusion to avoid dependency check in Business Central

		<dependency>
			<groupId>org.jbpm</groupId>
			<artifactId>jbpm-case-mgmt</artifactId>
			<version>6.5.0.Final-redhat-7</version>
			<exclusions>
				<exclusion>
					<groupId>org.kie</groupId>
					<artifactId>kie-internal</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.drools</groupId>
					<artifactId>drools-core</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

Without exclusion you get the following warning:

    WARN  [org.kie.workbench.common.services.backend.builder.ClassVerifier] (EJB default - 6) Verification of class org.drools.core.builder.conf.impl.JaxbConfigurationImpl failed and will not be available for authoring.
    Underlying system error is: com/sun/tools/xjc/Options. Please check the necessary external dependencies for this project are configured correctly.

## Coding
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

