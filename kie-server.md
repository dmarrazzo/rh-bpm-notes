# Kie Server REST API

## List of deployed processes

    GET
    <hostname>:<port>/kie-server/services/rest/server/queries/processes/definitions

Example result:

    {
        "processes": [
            {
                "process-id": "job-redo.RedoProc",
                "process-name": "RedoProc",
                "process-version": "1.0",
                "package": "org.jbpm",
                "container-id": "redo"
            },
        ]
    }

## Create a process instance:

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/<process-id>/instances

## List the process instances

    GET
    <hostname>:<port>/kie-server/services/rest/server/queries/processes/instances


## Delete a process instance

    DELETE
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/instances/<process-instance-id>

## Send a signal

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/instances/signal/<signal-ref>

Sample payload:

    { "test" : {"java.lang.String" : "ok"}}

## List available signal for an instance

    GET
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/instances/<process-instance-id>/signals

## Get the variables

    GET
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/instances/<process-instance-id>/variables

## Set the variables

    POST
    <hostname>:<port>/kie-server/services/rest/server/containers/redo/processes/instances/<process-instance-id>/variables
