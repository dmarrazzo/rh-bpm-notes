Human Tasks
===========

## Properties
Syntax to add variables in **subject** or **description**

E.g.

    Escalation for Artwork Processing #{defaultDataInput}

In a multi instance loop the human task description can access to the instance data: ``#{miDataInputX}``

## Authentication

Users are listed in the following file:

    <EAP_HOME>/standalone/configuration/application-users.properties

Roles are defined in the following file:

    <EAP_HOME>/standalone/configuration/application-roles.properties


### Default Users information

The default implementation [DefaultUserInfo](https://github.com/kiegroup/jbpm/blob/6.5.x/jbpm-human-task/jbpm-human-task-core/src/main/java/org/jbpm/services/task/identity/DefaultUserInfo.java) reads the configuration from:

    EAP_HOME/standalone/deployments/business-central.war/WEB-INF/classes/userinfo.properties

Example content:

    Luke\ Cage=luke@domain.com:en-UK:luke

It can be changed in `EAP_HOME/standalone/deployments/business-central.war/WEB-INF/beans.xml`


## ActorId

Add as input parameter of the HT
You can leverage the initiator variable:

 - declare `initiator` as `String`

## Due date

It's possible to specify a `DueDate` as parameter following ISO standard duration format.

Examples:

 - `P3Y6M4DT12H30M5S` represents a duration of "three years, six months, four days, twelve hours, thirty minutes, and five seconds".
 - `P2D` represents 2 days

[Wikipedia duration standard format](https://en.wikipedia.org/wiki/ISO_8601#Durations)

## Swim lane assignment

The second task in the swim lane is assigned to the same user that performed the previous task.

[AbstractHTWorkItemHandler AutoClaim](https://github.com/kiegroup/jbpm/blob/6.5.x/jbpm-human-task/jbpm-human-task-workitems/src/main/java/org/jbpm/services/task/wih/AbstractHTWorkItemHandler.java#L218)

## Assignment Rules

Assignment rules are rules executed automatically when a Human Task is created or completed. This mechanism can be used, for example, to assign a Human Task automatically to a particular user of a group or prevent a user from completing a Task if data is missing.

Create a file that will contain the rule definition on the Business Central classpath (the recommended location is DEPLOY_DIR/standalone/deployments/business-central.war/WEB-INF/classes/):

default-add-task.drl with the rules to be checked when the Human Task is created
default-complete-task.drl with the rules to be checked when the Human Task is completed

[TaskServiceRequest](https://github.com/kiegroup/jbpm/blob/master/jbpm-human-task/jbpm-human-task-core/src/main/java/org/jbpm/services/task/rule/TaskServiceRequest.java)



## Special Roles

The user `Administrator` and all users in the group `Administrators`, can see and manage all the task. (This name can be configure through these system properties: `org.jbpm.ht.admin.user`, `org.jbpm.ht.admin.group`.

In order to define a specific administration group for a task, the developer has to define either `BusinessAdministratorId` or `BusinessAdministratorGroupId` as task input parameter.

If a task define the `ExcludedOwnerId`, this user is removed by the potential owner list.

## Notifications

https://access.redhat.com/solutions/885393

## 4 eye principle

And why not to rely on potential owners and excluded owners that come with WS-HT spec that we do have support for?

the first task that is completed returns `ActorId` data output that represents the actual onwer who completed the task, then map this to variable and use on next task as excluded owner (via data input mapping, parameter name: `ExcludedOwnerId`) so when you query for tasks (via task service queries that take into consideration excluded owners) you won’t see that task and thus won’t be able to work on it.

[https://github.com/droolsjbpm/jbpm/blob/6.5.x/jbpm-human-task/jbpm-human-task-workitems/src/main/java/org/jbpm/services/task/wih/util/PeopleAssignmentHelper.java]()

[https://github.com/droolsjbpm/jbpm/blob/6.5.x/jbpm-human-task/jbpm-human-task-workitems/src/main/java/org/jbpm/services/task/wih/AbstractHTWorkItemHandler.java]()

It cannot implemented to a couple of issues.
