# Pre-defined Custom Queries

```json
{
  "queries": [
    {
      "query-name": "processesMonitoring",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select log.processInstanceId, log.processId, log.start_date, log.end_date, log.status, log.duration, log.user_identity, log.processVersion, log.processName, log.externalId from ProcessInstanceLog log",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmProcessInstanceLogs",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select log.id, log.nodeId, log.nodeName, log.nodeType, log.externalId, log.processInstanceId, log.log_date, log.connection, log.type, log.workItemId, log.referenceId, log.nodeContainerId, log.sla_due_date, log.slaCompliance from NodeInstanceLog log ",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmHumanTasks",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription, nil.sla_due_date, nil.slaCompliance from AuditTaskImpl t left join ProcessInstanceLog pil on pil.processInstanceId=t.processInstanceId left join NodeInstanceLog nil on nil.workItemId=t.workItemId",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmProcessInstances",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select log.processInstanceId, log.processId, log.start_date, log.end_date, log.status, log.parentProcessInstanceId, log.outcome, log.duration, log.user_identity, log.processVersion, log.processName, log.correlationKey, log.externalId, log.processInstanceDescription, log.sla_due_date, log.slaCompliance, COALESCE(info.lastModificationDate, log.end_date) as lastModificationDate, (select COUNT(errInfo.id) from ExecutionErrorInfo errInfo where errInfo.process_inst_id=log.processInstanceId and errInfo.error_ack=0) as errorCount from ProcessInstanceLog log left join ProcessInstanceInfo info on info.InstanceId=log.processInstanceId",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmExecutionErrorList",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select eri.ERROR_ACK, eri.ERROR_ACK_BY, eri.ERROR_ACK_AT, eri.ACTIVITY_ID, eri.ACTIVITY_NAME, eri.DEPLOYMENT_ID, eri.ERROR_DATE, eri.ERROR_ID, eri.ERROR_MSG, eri.JOB_ID, eri.PROCESS_ID, eri.PROCESS_INST_ID, eri.ERROR_TYPE from ExecutionErrorInfo eri",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "tasksMonitoring",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select p.processName, p.externalId, t.taskId, t.taskName, t.status, t.createdDate, t.startDate, t.endDate, t.processInstanceId, t.userId, t.duration from ProcessInstanceLog p inner join BAMTaskSummary t on (t.processInstanceId = p.processInstanceId) inner join (select min(pk) as pk from BAMTaskSummary group by taskId) d on t.pk = d.pk",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmHumanTasksWithVariables",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select tvi.taskId, tvi.name, tvi.value from TaskVariableImpl tvi",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmHumanTasksWithAdmin",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription ,oe.id, nil.sla_due_date, nil.slaCompliance, (select COUNT(errInfo.id) from ExecutionErrorInfo errInfo where errInfo.activity_id = t.taskId and errInfo.process_inst_id = pil.processInstanceId and errInfo.error_ack = 0 and errInfo.error_type = 'Task') as errorCount from AuditTaskImpl t  left join ProcessInstanceLog pil on pil.processInstanceId = t.processInstanceId left join PeopleAssignments_BAs ba on t.taskId = ba.task_id left join OrganizationalEntity oe on ba.entity_id = oe.id left join NodeInstanceLog nil on nil.workItemId=t.workItemId",
      "query-target": "FILTERED_BA_TASK",
      "query-columns": {}
    },
    {
      "query-name": "jbpmRequestList",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select ri.id, ri.timestamp, ri.status, ri.commandName, ri.message, ri.businessKey, ri.retries, ri.executions, pil.processName, pil.processInstanceId, pil.processInstanceDescription, ri.deploymentId from RequestInfo ri left join ProcessInstanceLog pil on pil.processInstanceId=ri.processInstanceId",
      "query-target": "CUSTOM",
      "query-columns": {}
    },
    {
      "query-name": "jbpmHumanTasksWithUser",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription , oe.id, eo.entity_id, nil.sla_due_date, nil.slaCompliance from AuditTaskImpl t left join PeopleAssignments_PotOwners po on t.taskId=po.task_id left join OrganizationalEntity oe on po.entity_id=oe.id left join ProcessInstanceLog pil on pil.processInstanceId=t.processInstanceId left join PeopleAssignments_ExclOwners eo on t.taskId=eo.task_id left join NodeInstanceLog nil on nil.workItemId=t.workItemId",
      "query-target": "FILTERED_PO_TASK",
      "query-columns": {}
    },
    {
      "query-name": "jbpmProcessInstancesWithVariables",
      "query-source": "${org.kie.server.persistence.ds}",
      "query-expression": "select vil.processInstanceId, vil.processId, vil.id, vil.variableId, vil.value from VariableInstanceLog vil where vil.id in (select MAX(v.id) from VariableInstanceLog v group by v.variableId, v.processInstanceId)",
      "query-target": "CUSTOM",
      "query-columns": {}
    }
  ]
}
```

## Audit tables

ProcessInstanceLog
NodeInstanceLog
VariableInstanceLog
AuditTaskImpl
BAMTaskSummary

### Info (Audit?)

ProcessInstanceInfo 
TaskVariableImpl
ExecutionErrorInfo
RequestInfo
OrganizationalEntity
PeopleAssignments_PotOwners
PeopleAssignments_ExclOwners

## Not based on Audit

jbpmHumanTasksWithVariables


## Other related information

- https://itnext.io/event-driven-microservices-with-spring-boot-and-activemq-5ef709928482