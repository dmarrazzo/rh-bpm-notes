# How to use KieServer REST API

A complete sample of how to use the remote KieServer to run rules and get back facts.
The example leverage the Jave client API backed by REST services.

  ServiceResponse<ExecutionResults> response = ruleClient.executeCommandsWithResults(CONTAINER, command);
