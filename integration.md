# Work Item Handler

To get the icon place the png in the `global` folder of the process project.

Init MVEL parameters:

        ksession
        taskService
        runtimeManager
        classLoader
        entityManagerFactory

Service registry:
https://docs.jboss.org/jbpm/release/6.5.0.Final/jbpm-docs/html_single/#d0e29362

# Service Task

* Service implementation: **Java** or **Webservices** (to avoid webservices, because there are more options with "Web Service" WIH)
* Service interface: fully qualified Java class name
* Service operation: name of the method (static method)
* Add a **input** `Parameter` that match the name of method parameter.

Example of method implementation:

```
	public static Object call(Object param) throws Exception {

		return param;
	}
```

## Handling input/output parameters

1. Edit data I/O (assignments)
2. Add the following data input and assignments:

  | NAME          | DATA TYPE | SOURCE           |
  |---------------|-----------|------------------|
  | Parameter     | String    | info             |
  | ParameterType | String    | java.lang.String |

3. Add the following data output and assignments:

  | NAME          | DATA TYPE | SOURCE           |
  |---------------|-----------|------------------|
  | Result        | String    | info             |


Internal implementation: 
[https://github.com/kiegroup/jbpm/blob/master/jbpm-bpmn2/src/main/java/org/jbpm/bpmn2/handler/ServiceTaskHandler.java]()

# Web Services

create a maven project

copy the WSDL in the `resources` folder

create in the project root the `jaxb-bindings.xml` with the following content:

    <?xml version="1.0" encoding="UTF-8"?>
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
    	xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
    	elementFormDefault="qualified" attributeFormDefault="unqualified"
    	jaxb:extensionBindingPrefixes="xjc" jaxb:version="1.0">
    	<xs:annotation>
    		<xs:appinfo>
    			<jaxb:globalBindings>
    				<xjc:serializable />
    			</jaxb:globalBindings>
    		</xs:appinfo>
    	</xs:annotation>
    </xs:schema>

launch wsconsume.sh

    $ <EAP_HOME>/bin/wsconsume.sh -b jaxb-bindings.xml -k -n -s src/main/java/ src/main/resources/POCJBSS.WSDL
    $ rm -rf output


Recent webservices uses bare type (not wrapped), so in order to match the service signature you have to transform the wrapper class in an object's array.

    requestArray = new Object[] {
    	request.getField1(),
    	request.getField2(),
    	request.getField3()
    };

### parameters

  - URL = http url for WSDL
  - endpoint = `location` of `soap:address`
  - mode = `SYNC`
  - interface = `name` of `portType`
  - operation = `name` of `operation`
  - namespace = `targetNamespace`

# Email
Test:

[Fake SMTP server/client](https://nilhcem.github.io/FakeSMTP/)

Configure at project level the WorkitemHandler:

1. Open Project Editor > Deployment Descriptor


    new org.jbpm.process.workitem.email.EmailWorkItemHandler("localhost", "8086","me@localhost","password")



# Call REST service

Use the REST Workitem

![REST palette](./imgs/rest-service_001.png)

## Configure the assignment
Data Input:

- Url - resource location to be invoked - mandatory
- Method - HTTP method that will be executed - defaults to GET
- ContentType - data type in case of sending data - mandatory for POST,PUT
- Content - actual data to be sent - mandatory for POST,PUT
- ConnectTimeout - connection time out - default to 60 seconds
- ReadTimeout - read time out - default to 60 seconds</li>
- Username - user name for authentication - overrides one given on handler initialization)
- Password - password for authentication - overrides one given on handler initialization)
- AuthUrl - url that is handling authentication (usually j_security_check url)
- HandleResponseErrors - optional parameter that instructs handler to throw errors in case of non successful response codes (other than 2XX)
- ResultClass - fully qualified class name of the class that response should be transformed to, if not given string format will be returned

Data Output:

- Result: the target DTO

**DTO** stands for *Data Transfer Object*: a Java Object that will be used to map the data send from and by the rest service.

![Data I/O](./imgs/rest-service_002.png)

If you want disregard some json properties, add the following annotation to the Java DTO:

    @org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)

This will say to the JSON mapper engine to ignore the JSON properties that are not present in the DTO, otherwise you'll get an exception.

## Dealing with the Content Type header property
Usually, REST service should declare how they serialize the data through the Header property `Content-Type` that in most case will assume the following values:

- `application/json`
- `application/xml`

Some REST services return a more complex Content-Type to add more details.

E.g. `Content-Type: application/json;charset=utf-8`

Unfortunately, the standard REST Workitem handler (WIH) is not able to handle this situation.
Here you will found a modified version of the WIH that address the problem.

[Improved REST WIH](./samples/wih/rest-wih/README.md)

[Implementation](https://github.com/kiegroup/jbpm/tree/6.5.x/jbpm-workitems/src/main/java/org/jbpm/process/workitem/rest)

# EJB

https://access.redhat.com/solutions/396853#eap6client

https://github.com/selrahal/jbpm-rest
