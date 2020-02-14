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

## Troubleshooting

```
org.drools.scenariosimulation.backend.runner.IndexedScenarioException: #3: null(/home/donato/git/demo/rhdm-qlb-loan/qlb-loan-application-repo/target/test-classes/com/redhat/demo/qlb/loan_application/eligibility-test.scesim)
	at org.drools.scenariosimulation.backend.runner.AbstractScenarioRunner.singleRunScenario(AbstractScenarioRunner.java:112)
```

Lack of dependecy 

```xml
    <dependency>
      <groupId>org.drools</groupId>
      <artifactId>drools-decisiontables</artifactId>
      <version>7.30.0.Final-redhat-00003</version>
      <scope>test</scope>
    </dependency>
```

### Error

```
 Unknown resource type: ResourceType = 'jBPM BPMN2 Language'
```

Lack of dependecy 

```xml
    <dependency>
      <groupId>org.jbpm</groupId>
      <artifactId>jbpm-bpmn2</artifactId>
    </dependency>
```
