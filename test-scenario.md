# Test scenario

## Debugging a test scenario

1. Launch Maven test in debug mode

```sh
mvn -Dmaven.surefire.debug test
```

2. Create a breakpoint in `AbstractRunnerHelper`

- package: `org.drools.workbench.screens.scenariosimulation.backend.server.runner`
- method: `run`
- line: `validateAssertion(scenarioRunnerData.getResults(),scenario);

3. In your Java IDE launch debug attaching the Maven process that is listening on port 5005

- Example of configuration in vscode:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug (Attach) - mvn test",
            "request": "attach",
            "hostName": "localhost",
            "port": "5005"
        }
    ]
}
```

4. Inspect the variable `scenarioRunnerData`

- `results` contains the list of execution tests
- `results[i].factMappingValue.rawValue` shows the expected value
- `results[i].resultValue` shows the actual value
