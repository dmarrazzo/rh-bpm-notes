# Forms

The form MUST match the variable name of the process

Internal BPM Forms has special requirements for the Data Types (Class definition) 

 - no inheritance
 - always default constructor (with no parameter)

## Field width

In order to define field more large use the **CSS Style** field.

E.g.

    width:400px


## Select

Example of `range value`:

    {supplier1,supplier1;supplier2,supplier2;supplier3,supplier3}
    

## Default value for a form field

Example of `Formula`:

    =return "ok";

## Hide a field based on checkbox flag

In the checkbox properties add this code to **on change script code**

    $("#order_rejectionReason").toggle(!this.checked);

In the example `order_rejectionReason` is the **field name**.

## Data provider

[https://access.redhat.com/solutions/1487113]()

## Attachment

To make your process manage documents you have to define your process variables as usual using the Custom Type `org.jbpm.document.Document`. Each variable defined as Document will be shown on the form as a FILE input.
    
Authoring view and edit the `kie-deployment-descriptor.xml` file located on `<yourproject>/src/main/resources/META-INF` and add your Document Marshalling Strategy to the `<marshalling-strategies>` list like this:

    <marshalling-strategies>
        <marshalling-strategy>
          <resolver>reflection</resolver>
          <identifier>
            org.jbpm.document.marshalling.DocumentMarshallingStrategy
          </identifier>
        </marshalling-strategy>
    </marshalling-strategies>

[https://docs.jboss.org/jbpm/release/6.5.0.Final/jbpm-docs/html/ch13.html#sect-formmodeler-attachments]()

From version 7.x, to manage Document Collections (`org.jbpm.document.service.impl.DocumentCollectionImpl`)

```xml
    <marshalling-strategies>
        <marshalling-strategy>
            <resolver>mvel</resolver>
            <identifier>new org.jbpm.document.marshalling.DocumentCollectionImplMarshallingStrategy(new org.jbpm.document.marshalling.DocumentMarshallingStrategy())</identifier>
        </marshalling-strategy>
    </marshalling-strategies>
```

### Dependencies

Add the dependencies to the project:

    <dependency>
    	<groupId>org.jbpm</groupId>
    	<artifactId>jbpm-document</artifactId>
    	<scope>provided</scope>
    </dependency>

**REMINDER:** Add the `jbpm-document-<release>.jar` to your kieserver libs.

### Client API to deal with Documents

Kie client API init:

```java
documentClient = client.getServicesClient(DocumentServicesClient.class);
```

Create a document

```java
document = DocumentInstance.builder()
        .name("first document")
        .size(contentBytes.length)
        .lastModified(new Date())
        .content(contentBytes)
        .build();
```

**Further code samples:** [https://github.com/kiegroup/droolsjbpm-integration/blob/master/kie-server-parent/kie-server-tests/kie-server-integ-tests-jbpm/src/test/java/org/kie/server/integrationtests/jbpm/DocumentServiceIntegrationTest.java]()

## Articles

[https://www.linkedin.com/pulse/does-bpm-need-head-donato-marrazzo]()


