Coding Tips
===========

MVEL
-----------

http://mvel.documentnode.com/


Variable substitution
---------------------------------

There are many place where you can place MVEL expressions.
For example in human task subject field:

    #{variable}

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

## Class visibility in Business Central

The file `package-names-white-list` can be used to declare visible packages.
When you declare a package others become invisible to the BC.

The use of this file allows a developer to narrow down the group of facts that are loaded and are therefore, visible. This helps in speeding up the loading of these facts while creating new rules. This file is created automatically on the creation of a new project in the root directory, along with the pom.xml and project.imports project files. For existing projects, you may create this file manually.


## Debugging technique

In order to understand the process actual logic at runtime, you can leverage the event listeners.
There is an already good implementation really useful for debugging purposes.


- Open the deployment descriptors and add an **event listener** entry:

    - Identifier: `org.drools.core.event.DebugProcessEventListener`
    - Resolver: `reflection`
    - Parameters: *none*



Otherwise, if you prefer have a proper debug session with your favourite java IDE


- Start the server in debug mode:

        ./standalone.sh --debug

- attach the IDE to the remote debugging port 8787.
- place your breakpoints and run the process

You can debug the process placing breakpoints on the event listeners.
Another (simpler) option is to leverage a simple utility class, e.g.:

```java
public static void debug(ProcessContext kcontext) {
    System.out.println("Util.debug()");
}
```

The designer place the method call in the "on entry" script of the task that he want to analyse (or "on exit" one).
The designer sets a breakpoint in that method and when the method is reached he can inspect and the `kcontext` information.

The latter technique has the drawback of being quite intrusive, because it requires an update of the process model. 

**Warning:** the process runs in a transaction context with a default time out of 120 seconds, so if the debugging activities last more than that, you should incur in a runtime exception.

## OpenShift debug

To configure JVM remote debugging on a JBoss container, add the following configuration to the JAVA_OPTS_APPEND environment variable of the container:


-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n


CRC snippet:

```yaml
spec:
  environment: rhpam-authoring
  objects:
    servers:
    - jvm:
        javaOptsAppend: -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n
```

Retrieve the list of running pods:

	oc get pods

Port-forward the a local port to the debugging port of the container, e.g.:

	oc port-forward ${pod-name} 8787:8787

Get the local classloader
--------------------------------------------

```java
LocalKieContainerMain.class.getClassLoader()
```


LocalDateTime serializzation issues
--------------------------------------------

In order to serialize LocalDateTime you can configure the related property in this way:

```java
@JsonSerialize(using=LocalDateTimeSerializer.class)
@JsonDeserialize(using=LocalDateTimeDeserializer.class)
@JsonFormat(shape = Shape.STRING)
private java.time.LocalDateTime startTime;
```

### Check the generated code

In order to debug the java code in the snippet you need to access to the generated code.

There is a system property that force the runtime to dump the code:
`-Ddrools.dump.dir=some/root/dir`




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

## Trigger a process from an other process / rule

To start a process from within a process instance you can use code
like this (not sure you can use this from within a rule, never tried
this):

```
    RuntimeManager rm =
    RuntimeManagerRegistry.get().getManager(deploymentId);
    RuntimeEngine engine = rm.getRuntimeEngine(EmptyContext.get());
    KieSession ksession = engine.getKieSession();
    try {
        ksession.startProcess(processId, paramMap);
    } finally {
        rm.disposeRuntimeEngine(engine);
    }
```

Inside a kieserver:

```
    KieServerImpl kieServer = KieServerLocator.getInstance();
    KieServerExtension ext = ((KieServerImpl)kieServer).getServerRegistry().getServerExtension("jBPM");
    ProcessService processService = (ProcessService) ext.getServices().stream().filter(service -> service instanceof ProcessService).findFirst().get();
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("eventId", eventId);
    processService.startProcess(deploymentId ,processId, paramMap);
```

Inside a WorkItemHandler, `runtimeManager` is provided at initialization time:


	RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
	KieSession kieSession = runtimeEngine.getKieSession();
	Map<String, Object> startParams = new HashMap<>();
	startParams.put("businessId", "id-"+n);
	kieSession.startProcess(processName, startParams );

**Warning:** When `PER_PROCESS_INSTANCE` strategy in place, DON'T reuse the runtime engine, but get a new one for each new process to start.


Remind to **free up** the resources:

    runtimeManager.disposeRuntimeEngine(runtimeEngine);

Retrieve the runtimeEngine when `PER_PROCESS_INSTANCE` strategy in place:

	Context<?> context = new ProcessInstanceIdContext(parentProcessInstanceId);
	RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(context);


Import dependencies using `provided` for the scope.

```
    <dependency>
      <groupId>org.kie</groupId>
      <artifactId>kie-api</artifactId>
      <scope>provided</scope>
      <optional>true</optional>
    </dependency>
```

**Optional** tag should avoid the propagation of the dependency in chain. It's useful in case the code is in the Business Central.

## Retrieve runtime manager

    KieRuntime kruntime = ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime();
    RuntimeManager manager = (RuntimeManager) kruntime.getEnvironment().get(EnvironmentName.RUNTIME_MANAGER);

## Retrieve the deployment id

Leveraging `kcontext`:

```java
    String deploymentId = (String) kcontext.getKieRuntime().getEnvironment().get(EnvironmentName.DEPLOYMENT_ID);
```

## Insert Variable in Working Memory of a Process 

Example

    kcontext.getKnowledgeRuntime().insert( application );

## Adding a signal listener

    RuleFlowProcessInstance processInstance = (...)
    
    InternalProcessRuntime processRuntime = (InternalProcessRuntime) processInstance.getKnowledgeRuntime().getProcessRuntime();
    SignalManager signalManager = processRuntime.getSignalManager();
    retryHandlingListener.register(signalManager);
    signalManager.addEventListener("test", listener);
    
## Catching a closing process instance

It's possible to ask a process to sent a signal when it complete:

    RuleFlowProcessInstance processInstance = (...)
    piImpl.setSignalCompletion(true);
    
Conventionally, it sends a signal with the following *SignalRef*: `processInstanceCompleted:#{processInstanceId}`.

The event payload is the `RuleFlowProcessInstance`, where it's possible to retrieve all the information on the closing instance.

## Multi thread tolerance

During the development of BRMS 6.3 and 6.4, the engine was redesigned to better coordinate different threads (both created by user and internal to the engine itself) accessing a KieSession. In particular, they simplified the concurrency model of the engine, with the introduction of that state machine, and this allow to both increase performances in multithreaded environments and be more confident about the robustness of the engine in heavily concurrent use cases.

KieBase and KieSession have been designed to be thread-safe.
Dynamic changes to a KieBase are now coordinated with the state machines of the KieSessions formerly created by the same KieBase, so also incremental compilation can be considered thread-safe.

KieSessions can be shared between different threads.

It's better to avoid the execution of long and blocking operations inside the RHS but this is not related with thread-safety. This only related to performance reasons, because also the invocation of a RHS is blocking and synchronous, so having a particularly slow RHS will have the effect of slowing down the whole engine.

KieSession is single threaded (v7 introduces multi-threading capabilities). So any blocking operation in the RHS blocks the full session and all the rule evaluation in that session. So blocking operations in the RHS essentially kills performance of your rules engine.

## Timers

since version 6 timers can be configured with valid ISO8601 date format that supports both one shot timers and repeatable timers. Timers can be defined as date and time representation, time duration or repeating intervals

- Date - 2013-12-24T20:00:00.000+02:00 - fires exactly at Christmas Eve at 8PM
- Duration - PT1S - fires once after 1 second
- Repeatable intervals - R/PT1S - fires every second, no limit, alternatively R5/PT1S will fire 5 times every second

### Duration format

 - `P3Y6M4DT12H30M5S` represents a duration of "three years, six months, four days, twelve hours, thirty minutes, and five seconds".
 - `P2D` represents 2 days

[Wikipedia duration standard format](https://en.wikipedia.org/wiki/ISO_8601#Durations)

Service Registry
---------------------------------------------------------------------------------------------

Service Registry must be used in a kie-server (CDI not allowed):

[ServiceRegistry](https://github.com/kiegroup/jbpm/blob/master/jbpm-services/jbpm-services-api/src/main/java/org/jbpm/services/api/service/ServiceRegistry.java)

Dependency:

```xml
<dependency>
    <groupId>org.jbpm</groupId>
    <artifactId>jbpm-services-api</artifactId>
    <scope>provided</scope>
</dependency>
```

Usage example:

```java
RuntimeDataService runtimeDataService = (RuntimeDataService) ServiceRegistry.get()
        .service(ServiceRegistry.RUNTIME_DATA_SERVICE);

UserTaskAdminService userTaskAdminService = (UserTaskAdminService) ServiceRegistry.get()
        .service(ServiceRegistry.USER_TASK_ADMIN_SERVICE);

QueryService queryService = (QueryService) ServiceRegistry.get().service(ServiceRegistry.QUERY_SERVICE);
```

Local Container
---------------------------------------------------------------------------------------------

Retrieve the kieContainer from the classpath

```java
    private static KieServices ks = KieServices.Factory.get();
    private static KieContainer kieContainer = ks.getKieClasspathContainer(BKM.class.getClassLoader());
```
