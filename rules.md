Business Rule Task
==========================

# Input and Output parameters
Input are placed in working memory
Output are deleted by the working memory
[code][1]

#Dependency

in the parent 

    <dependencyManagement>
    	<dependencies>
    		<dependency>
    			<groupId>org.drools</groupId>
    			<artifactId>drools-bom</artifactId>
    			<version>6.4.0.Final-redhat-8</version>
    			<type>pom</type>
    			<scope>import</scope>
    		</dependency>
    	</dependencies>
    </dependencyManagement>

in the module

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

# Testing the project Java Project

- Define the Knowledge base  

    <kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    
        <kbase name="drlRules" equalsBehavior="equality" eventProcessingMode="cloud" default="true">
            <ksession name="ksessionDrlRules" default="true" type="stateful" clockType="realtime" />
        </kbase>
        
    </kmodule>

-     







[1]:https://github.com/droolsjbpm/jbpm/blob/738d191d338dab3e8baceeaf6fe31556b81fe07f/jbpm-flow/src/main/java/org/jbpm/workflow/instance/node/RuleSetNodeInstance.java



