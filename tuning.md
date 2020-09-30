# Tuning


BPM manages asynchronous tasks via the Executor Service; for a better explanation refer to this link: 

http://mswiderski.blogspot.it/2015/08/shift-gears-with-jbpm-executor.html 


Executor Service is built as a java executor
https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html 

These are the most used configurations for executor service and JMS:
Increase the number of maxSession for the executor MDB 
Increase the mdb pool size
Increase the number of connections for the connection for JmsXA connection factory. 


Increase the maxSession for the server side MDB (that receives the executor message):
kie-server.war/WEB-INF/ejb-jar.xml

```
    <message-driven>
      <ejb-name>KieExecutorMDB</ejb-name>
      <ejb-class>org.kie.server.jms.executor.KieExecutorMDB</ejb-class>
      <transaction-type>Bean</transaction-type>
      <activation-config>
        <activation-config-property>
          <activation-config-property-name>destinationType</activation-config-property-name>
      <activation-config-property-value>javax.jms.Queue</activation-config-property-value>
        </activation-config-property>
        <activation-config-property>
          <activation-config-property-name>destination</activation-config-property-name>
          <activation-config-property-value>java:/queue/KIE.SERVER.EXECUTOR</activation-config-property-value>
        </activation-config-property>
        <activation-config-property>
                <activation-config-property-name>maxSession</activation-config-property-name>
                <activation-config-property-value>100</activation-config-property-value>
        </activation-config-property>
      </activation-config>
    </message-driven>
```

Increase the EJB pool size for MDBs. This is also for the server side MDB:
standalone.xml

```
<pools>
    <bean-instance-pools>
        <strict-max-pool name="slsb-strict-max-pool" max-pool-size="100" instance-acquisition-timeout="5" instance-acquisition-timeout-unit="MINUTES"></strict>
        <strict-max-pool name="mdb-strict-max-pool" max-pool-size="100" instance-acquisition-timeout="5" instance-acquisition-timeout-unit="MINUTES"></strict>
    </bean-instance-pools>
</pools>
```

Increase the JmsXA connection factory thread pool size - this is for the client that sends the JMS messages:
standalone.xml

```
<pooled-connection-factory name="hornetq-ra">
    <transaction mode="xa"></transaction>
    <connectors>
        <connector-ref connector-name="in-vm"></connector>
    </connectors>
    <entries>
        <entry name="java:/JmsXA"></entry>
    </entries>
    <scheduled-thread-pool-max-size>100</scheduled-thread-pool-max-size>
    <thread-pool-max-size>100</thread-pool-max-size>
</pooled-connection-factory>
```

# DRL Tuning

Red Hat Process Automation Manager include a powerful rule engine based on the well known Drools project.

Tuning DRL to get the best performances is not trivial task. The following project is a very valuable tool:

https://github.com/tkobayas/MetricLogUtils/wiki/How-to-use-MetricLogUtils

### References

[Tune async execution in Kie Server](http://mswiderski.blogspot.it/2017/11/tune-async-execution-in-kie-server.html)

- https://access.redhat.com/solutions/3003791
- https://access.redhat.com/solutions/3109201 
- https://access.redhat.com/solutions/2455451

### EAP 7.0 specific configurations

```
<pooled-connection-factory name="activemq-ra" transaction="xa" thread-pool-max-size="50" entries="java:/JmsXA java:jboss/DefaultJMSConnectionFactory" connectors="in-vm"/>
```
[maxSession and MDB pool size in JBoss EAP 7.x](https://access.redhat.com/solutions/2955481)

Starting with JBoss EAP 7 setting the MDB pool size is not always enough. The algorithm that calculates the number of threads that are allocated to MDB pool is 8 * cpu_cores. This may result in insufficient number of threads to be allocated to service the MDB pool.

To override default, add this system property:

```
-Dactivemq.artemis.client.global.thread.pool.max.size=50
```

Due to a bug in current JBoss EAP 7 it is necessary to edit the configuration file directly and remove the derive-size from the mdb-strict-max-pool and set the pool size accordingly (see example below):

```
<pools>
 <bean-instance-pools>
  <strict-max-pool name="slsb-strict-max-pool" derive-size="none" instance-acquisition-timeout="5" instance-acquisition-timeout-unit="MINUTES"/>
  <strict-max-pool name="mdb-strict-max-pool" max-pool-size="50" instance-acquisition-timeout="5" instance-acquisition-timeout-unit="MINUTES"/>
 </bean-instance-pools>
</pools>
```


### Tuning the executor


Tuning parameters:

- `org.kie.executor.pool.size` : the number of concurrent threads for the executor, defaults to 1
- `org.kie.executor.interval` : interval for the executor. Defaults to 3
- `org.kie.executor.timeunit` : timeunit for the interval. Defaults to SECONDS


Increase the datasource pool size accordingly:

```
<datasource ...>
(...)
    <pool>
        <max-pool-size>30</max-pool-size>
    </pool>
</datasource>
```

#### Disabling the JMS Executor mode:

**This setting should be avoided because decreases performances**


    org.kie.executor.jms=false
