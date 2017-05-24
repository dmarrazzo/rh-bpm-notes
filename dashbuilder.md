Dashbuilder
============================================================================

Administration
----------------------------------------------------------------------------

It's possible to give users different permissions for each workspace

### Manage the Data provider

Navigate ** Showcase > Administration **




Data providers
----------------------------------------------------------------------------

### Tasks duration

jBPM Tasks Duration

select ts.taskname as taskname, count(ts.taskid) as taskcount, min(ts.duration) as tminduration, avg(ts.duration) as tavgduration, max(ts.duration) as tmaxduration from BAMTaskSummary ts left join ProcessInstanceLog ps on (ts.processinstanceid=ps.processinstanceid) where ts.duration is not null and {sql_condition, optional, ps.processname, processname} and {sql_condition, optional, ps.status, status} and {sql_condition, optional, ps.start_date, start_date} and {sql_condition, optional, ps.end_date, end_date} and {sql_condition, optional, ps.processversion, processversion} and {sql_condition, optional, ts.userid, userid} and {sql_condition, optional, ts.taskname, taskname} and {sql_condition, optional, ts.createddate, createddate} and {sql_condition, optional, ts.enddate, enddate} and {sql_condition, optional, ts.status, taskstatus} group by ts.taskname order by ts.taskname asc

select ts.taskname as taskname, count(ts.taskid) as taskcount, min(ts.duration) as tminduration, avg(ts.duration) as tavgduration, max(ts.duration) as tmaxduration from BAMTaskSummary ts left join ProcessInstanceLog ps on (ts.processinstanceid=ps.processinstanceid) where ts.duration is not null group by ts.taskname order by ts.taskname asc

### Hacking

UPDATE BAMTASKSUMMARY AS BT SET DURATION=210334*(SELECT TASKID from BAMTASKSUMMARY where TASKID=BT.TASKID) Where DURATION is NOT null

UPDATE BAMTASKSUMMARY AS BT SET CREATEDDATE=DATEADD('DAY', -(SELECT PK % 5 from BAMTASKSUMMARY where TASKID=BT.TASKID), TODAY) Where DURATION is NOT null


UPDATE PROCESSINSTANCELOG AS PIL SET END_DATE=DATEADD('DAY', -(SELECT ID % 3 from PROCESSINSTANCELOG where ID=PIL.ID), TODAY) WHERE END_DATE IS NOT NULL

BPM Source Tables
----------------------------------------------------------------------------

BAMTaskSummary
ProcessInstanceLog

Database table to save
----------------------------------------------------------------------------

DASHB_ALLOWED_PANEL
DASHB_DATA_PROVIDER
DASHB_DATA_PROVIDER_I18N
DASHB_DATA_SOURCE
DASHB_DATA_SOURCE_COLUMN
DASHB_DATA_SOURCE_TABLE
DASHB_GRAPHIC_RESOURCE
DASHB_INSTALLED_MODULE
DASHB_KPI
DASHB_KPI_I18N
DASHB_PANEL
DASHB_PANEL_HTML
DASHB_PANEL_HTML_I18N
DASHB_PANEL_INSTANCE
DASHB_PANEL_PARAMETER
DASHB_PERMISSION
DASHB_SECTION
DASHB_SECTION_I18N
DASHB_WORKSPACE
DASHB_WORKSPACE_HOME
DASHB_WORKSPACE_PARAMETER

** don't copy: **
DASHB_CLUSTER_NODE


H2
============================================================================

Backup
----------------------------------------------------------------------------

The recommended way to backup a database is to create a compressed SQL script file. This will result in a small, human readable, and database version independent backup. Creating the script will also verify the checksums of the database file. The Script tool is ran as follows:


### Backup the dashbuilder tables for BPM

    SCRIPT DROP TO '~/H2-DASHBUILDER-DUMP.SQL' TABLE BAMTaskSummary, ProcessInstanceLog

### Command line

    java org.h2.tools.Script -url jdbc:h2:~/test -user sa -script test.zip -options compression zip


Restore
----------------------------------------------------------------------------

### Backup the dashbuilder tables for BPM

    RUNSCRIPT FROM '~/H2-DASHBUILDER-DUMP.SQL' 


### Command line

    java org.h2.tools.RunScript -url jdbc:h2:~/test -user sa -script test.zip -options compression zip

Example running in BPM:

    java -cp bin/EAP-7/modules/system/layers/base/com/h2database/h2/main/h2-1.3.173.redhat-2.jar org.h2.tools.RunScript -url jdbc:h2:~/bin/EAP-7/bin/h2-filestore -user sa -password sa -script H2-DASHBUILDER-DUMP.SQL

