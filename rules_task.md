Business Rule Task
==================

## Input and Output parameters
Input are placed in working memory
Output are deleted by the working memory
[code][1]

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



