# Asynchronous Jobs

Some findings:

1. If you specify "Is Async" for a task, it is executed in another thread by the executor
2. if the service get an runtime exception the executor repeat the execution for 3 times, then it puts the job in error state
3. in the Jobs page (Deploy > Jobs), you can find the job in the error tab.
4. the user can requeue the job, so the executor tries again the job execution.
5. from the Jobs page, you can stop and start the Executor, change the frequency and the threads (I think that it is implemented with a polling logic over the DB)
6. If I stop the executor and launch a new process, for some strange reason the job is executed (regardless the executor stop status!) but if the service raise an exception, it is marked as "retrying" and is not processed again till the executor is started again

# Asynchronous Workitems

[make-your-work-asynchronous](http://mswiderski.blogspot.it/2013/08/make-your-work-asynchronous.html)

A process can have two kinds of asynchronous tasks:
Task with the "Is Async" property set to true - this generates a proxy called "AsyncSignalEventCommand" (an asynchronous job) that is charge to execute the task. In this case, when the job is executed the actual process instance variables are retrieved and passed to the synchronous WorkItemHandler implementation.
Task with an [AsyncWorkItemHandler][1] Implementation (or the command pattern) - in this case an asynchronous job is generated with the input payload serialized at creation time. When the job is executed the payload is always the same regardless the actual values of process variables.

[How do I use job executor from a task in JBoss BPM Suite 6.1?](https://access.redhat.com/solutions/2142501)

# Executor configuration
Executor can work with or without JMS.
JMS is the preferred option but executor can work without it and it does for instance on Tomcat where there is no JMS provider out of the box.
you can disable JMS executor by system property `org.kie.executor.jms=false`

[How can I increase the number of running jobs using the JMS based executor in BPM Suite 6](https://access.redhat.com/solutions/3003791)

1. Increase the number of maxSession for the executor MDB (JmsAvailableJobsExecutor) in business-central.war/WEB-INF/ejb-jar.xml, the mdb pool size and also the number of connections for the connection for JmsXA connection factory.
2. Increase the EJB pool size for MDBs
3. Increase the JmsXA connection factory thread pool size

# Deployment Descriptor

While kmodule is mainly targeting on knowledge base and knowledge session basic configuration, deployment descriptors are considered more technical configuration. Following are the items available for configuration via deployment descriptors:


  * persistence unit name for runtime data
  * persistence unit for audit data
  * persistence mode (JPA or NONE)
  * audit mode (JPA, JMS, NONE)
  * runtime strategy (SINGLETON, PER_REQUEST, PER_PROCESS_INSTANCE)
  * list of event listeners to be registered
  * list of task event listeners to be registered
  * list of work item handlers to be registered
  * list of globals to be registered
  * marshalling strategies to be registered (for pluggable variable persistence)
  * required roles to be granted access to resources of the kjar
  * additional configuration options of knowledge session
  * additional environment entries for knowledge session
  * list of fully qualified class names that shall be added to the classes used for serialization by remote services
  * whether or not to limit the classes from the deployment used for serialization by the remote services

[https://docs.jboss.org/jbpm/release/6.5.0.Final/jbpm-docs/html/ch14.html#d0e15405]()

# Custom variable persistence

It is possible store the process variable in a DBMS table.
In order to achieve this result, you have to configure the *Data Object* as *Persistable*.

![Create Data Object](imgs/persistable_01.png)

Then you have to configure the *Persistence descriptor*:

1. From the **Project Settings** open the *Persistence descriptor*.

    ![Persistence descriptor](imgs/persistable_02.png)

2. Add all *Project persistable classes* to the persistence descriptor

    ![Persistence descriptor](imgs/persistable_03.png)

3. By default the new persistence unit points to the same BPM datasource, this means that the table will be created in the same DB of the BPM engine. In order to separate the process specific information from the BPM engine ones, it's a good practice to define a new datasource targeting a different DB. The drawback of the latter configuration is that the BPM datasource and the new one must be XA compliant, with the performance implication that a distributed transaction brings on the table.

## References

[jBPM 6 - store your process variables anywhere](http://mswiderski.blogspot.it/2014/02/jbpm-6-store-your-process-variables.html)


# Advanced Queries


## References

[Advanced Queries in jBPM](http://mswiderski.blogspot.it/2016/01/advanced-queries-in-jbpm-64.html)

[Advanced queries in KIE Server](http://mswiderski.blogspot.it/2016/01/advanced-queries-in-kie-server.html)


[1]: https://github.com/droolsjbpm/jbpm/blob/master/jbpm-services/jbpm-executor/src/main/java/org/jbpm/executor/impl/wih/AsyncWorkItemHandler.java

# Registering Manually  

In general, what is most likely happening is that you are registering handler manually via ksession and in case you use runtime manager and strategy other than singleton it will be not visible by other contexts. Looks like you are using per process instance strategy which would explain why it fails after starting subprocess - it gets new context - new ksession without handler being registered there. So you need to use RegisterableItemsFactory for registering handlers. That is set on RuntimeEnvironment used to create runtime manager, see here:
https://github.com/kiegroup/jbpm/blob/master/jbpm-services/jbpm-executor/src/test/java/org/jbpm/executor/impl/wih/AsyncContinuationSupportTest.java#L155-L169

# Correlation Key

Technically you probably can create a correlation key afterwards, it's basically an entry in the database that links both.  But in the public api it's only exposed when starting a process.  Using internal apis you can probably create a CorrelationKeyInfo object and persist it.  

correct, you should be able to create it and persist, though that does not much change compared to using variable. If I may ask why not assigning correlation key directly?

<https://github.com/kiegroup/jbpm/blob/d881f2a72bd4279d4277b294c2dbc6663d91c612/jbpm-persistence/jbpm-persistence-jpa/src/main/java/org/jbpm/persistence/processinstance/JPAProcessInstanceManager.java#L73-L91>

Audit 
===========================================================================

## Separate Runtime and BAM data

[https://issues.jboss.org/browse/JBPM-5211]()

# JMS Audit
setting system property

    -Djbpm.audit.jms.enabled=true

File `jbpm.audit.jms.properties` needs to be placed on application classpath (kie-server.war/WEB-INF/classes) as it’s a global setting

```
jbpm.audit.jms.connection.factory.jndi=java:/JmsXA
jbpm.audit.jms.queue.jndi=queue/KIE.AUDIT.ALL
```
For WebLogic AS add: `jbpm.audit.jms.transacted=false` 

[https://access.redhat.com/solutions/3301681]()

Log producer: 

[https://github.com/kiegroup/jbpm/blob/master/jbpm-audit/src/main/java/org/jbpm/process/audit/jms/AsyncAuditLogProducer.java]()

Issues
===========================================================================

## PerProcessInstanceRuntimeManager

https://access.redhat.com/solutions/1183403


## Error deserializing process instance

If you get the following exception, trying to abort a process instance:

```
java.io.IOException: Error deserializing process instance.
	at org.jbpm.marshalling.impl.AbstractProtobufProcessInstanceMarshaller.readProcessInstance(AbstractProtobufProcessInstanceMarshaller.java:479)

```

It's likely that you have in your Java model a reference to a class that is not present in the classpath.

In fact, you should find down in the stack trace, the evidence of the missing class:

```
Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.Slf4jLogger
	at java.lang.ClassLoader.findClass(ClassLoader.java:530)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at org.drools.core.common.ProjectClassLoader$DefaultInternalTypesClassLoader.loadType(ProjectClassLoader.java:394)
	at org.drools.core.common.ProjectClassLoader.loadType(ProjectClassLoader.java:172)
```

