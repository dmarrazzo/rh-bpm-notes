Rules
==================

If you are using Decision Manager in an embedded mode, you can transform a guided decision table into the DRL rules, for example:

```java
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTDRLPersistence;
// imports unrelated to marshalling omitted
// class and methods omitted

GuidedDecisionTable52 unmarshal =
  GuidedDTXMLPersistence.getInstance()
    .unmarshal(new String(
      Files.readAllBytes(Paths.get("src/main/resources/guidedTable.gdst"))
	 ));

String drl = GuidedDTDRLPersistence.getInstance().marshal(unmarshal);
System.out.println(drl);
```

Business Rule Task
==================

## Input and Output parameters
Input are placed in working memory and then remove
Output are disregarded
[RuleSetNodeInstance][1]

# New decoupled rule and decision WIH

http://mswiderski.blogspot.it/2017/04/control-business-rules-execution-from.html

## Runtime Strategies

Using **per process instance** as runtime strategy, you will find the same kiesession through all the interaction with the process. This will ensure that the working memory follow the process instance life cycle. The working memory is saved and survive the server restart.

## Testing the project Java Project

- Define the Knowledge base  

    <kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    
        <kbase name="drlRules" equalsBehavior="equality" eventProcessingMode="cloud" default="true">
            <ksession name="ksessionDrlRules" default="true" type="stateful" clockType="realtime" />
        </kbase>
        
    </kmodule>


## Dependencies

Some important dependencies:

		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-api</artifactId>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-decisiontables</artifactId>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-core</artifactId>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-workbench-models-guided-dtable</artifactId>
		</dependency>






[1]:https://github.com/droolsjbpm/jbpm/blob/738d191d338dab3e8baceeaf6fe31556b81fe07f/jbpm-flow/src/main/java/org/jbpm/workflow/instance/node/RuleSetNodeInstance.java

# Conditional Start Node

It's a nice way to trigger process logic based on rule logic.

Unfortunately, there's no way to get the object that matched the rule:

Usual start node:

https://github.com/kiegroup/jbpm/blob/master/jbpm-flow-builder/src/main/java/org/jbpm/compiler/ProcessBuilderImpl.java#L518

But for conditional start nodes, no input mappings are set, basically we're not calling addTriggerWithInputMapping here:

https://github.com/kiegroup/jbpm/blob/master/jbpm-bpmn2/src/main/java/org/jbpm/bpmn2/xml/StartEventHandler.java#L92

