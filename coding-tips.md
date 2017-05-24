Coding Tips
===========

## MVEL
http://mvel.documentnode.com/


## Variable substitution

There are many place where you can place MVEL expressions.
For example in human task subject field:

    #{varible}

## Variable

    kcontext.setVariable("list",list);

## On exit

    System.out.println(">>> exit: " + kcontext.getNodeInstance().getNodeName());

## Variable Mapping at assignment level

The task assignments are able to perform variable mapping using the MVEL notation.

E.g.:

![mapping](imgs/coding-tips-01.png)
 
## Get/Set variable by code

The following interface provides the full access to the variable:

    org.kie.api.runtime.process.WorkflowProcessInstance

This is a code sample:

	WorkflowProcessInstance processInstance = (WorkflowProcessInstance) runtimeEngine.getKieSession().getProcessInstance(processInstanceId);
	processInstance.getVariable(name);
	processInstance.setVariable(name, value);


## Debugging process

Add this event Listener:
org.drools.core.event.DebugProcessEventListener

## XStream utility

Constructor

	XStream xStream = new XStream();
	xStream.fromXML(xml, this);

String

	public String toString() {
		XStream xStream = new XStream();
		return xStream.toXML(this);
	}

## EJB client

	<properties>
		<version.bpms>6.4.0.Final-redhat-6</version.bpms>
	</properties>

(...)
	
		<dependency>
			<groupId>org.jbpm</groupId>
			<artifactId>jbpm-services-ejb-client</artifactId>
			<version>${bpm.version}</version>
			<scope>runtime</scope>
		</dependency>


