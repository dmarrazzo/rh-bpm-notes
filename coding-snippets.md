# Coding tips

## Variable substitution

    #{varible}

## Variable

    kcontext.setVariable("list",list);

## On exit

    System.out.println(">>> exit: " + kcontext.getNodeInstance().getNodeName());

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


