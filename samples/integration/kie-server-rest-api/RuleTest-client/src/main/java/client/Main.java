package client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssa.Test;
import ssa.Transaction;

public class Main {
	final static Logger log =  LoggerFactory.getLogger(Main.class);
	
	private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
	private static final String user = "donato";
	private static final String password = "donato";
	private static final String CONTAINER = "SSA:RuleTest:1.1-SNAPSHOT";

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ksFireAllRule();
		//ksStartRuleFlow();
		long end = System.currentTimeMillis();
		System.out.println("time elapsed: "+ (end-start));
	}

	@SuppressWarnings("rawtypes")
	public static void ksFireAllRule() {
		try {
			
			KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, user, password);
			// Marshalling
			Set<Class<?>> extraClasses = new HashSet<Class<?>>();	
			extraClasses.add(Transaction.class);
			extraClasses.add(Test.class);
			config.addExtraClasses(extraClasses);
			config.setMarshallingFormat(MarshallingFormat.XSTREAM);
			Map<String, String> headers = null;
			config.setHeaders(headers);
			
			KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
			RuleServicesClient ruleClient = client.getServicesClient(RuleServicesClient.class);

			
			KieCommands cmdFactory = KieServices.Factory.get().getCommands();
			List<Command> commands = new ArrayList<Command>();

			Transaction transaction = new Transaction();
			transaction.setAmount(102.0);
			
			commands.add(cmdFactory.newInsert(transaction, "transaction"));
			commands.add(cmdFactory.newAgendaGroupSetFocus("test"));
			commands.add(cmdFactory.newFireAllRules());
		    
			commands.add(cmdFactory.newGetObjects("objects"));
			BatchExecutionCommand command = cmdFactory.newBatchExecution(commands, "ksessionStateless");
			
			ServiceResponse<ExecutionResults> response = ruleClient.executeCommandsWithResults(CONTAINER, command);
			ExecutionResults results = response.getResult();

			if (results==null)
				throw new Exception(response.toString());
			
			Collection<String> identifiers = results.getIdentifiers();
			for (String identifier : identifiers) {
				Object fact = results.getValue(identifier);
				
				log.info("fact: {} {}", identifier, fact);
			}			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void ksStartRuleFlow() {
		try {
			
			KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, user, password);
			// Marshalling
			Set<Class<?>> extraClasses = new HashSet<Class<?>>();
			extraClasses.add(Transaction.class);
			config.addExtraClasses(extraClasses);
			config.setMarshallingFormat(MarshallingFormat.JSON);
			
			
			KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
			RuleServicesClient ruleClient = client.getServicesClient(RuleServicesClient.class);

			KieCommands cmdFactory = KieServices.Factory.get().getCommands();
			List<Command> commands = new ArrayList<Command>();

			Transaction transaction = new Transaction();
			transaction.setAmount(102.0);

			commands.add(cmdFactory.newInsert(transaction, "transaction"));
			commands.add(cmdFactory.newStartProcess("RuleTest.rule-flow"));
			BatchExecutionCommand command = cmdFactory.newBatchExecution(commands, "ksessionDrlRules");

			ServiceResponse<ExecutionResults> response = ruleClient.executeCommandsWithResults(CONTAINER, command);
			ExecutionResults results = response.getResult();
			if (results==null)
				throw new Exception(response.toString());
			
			Collection<String> identifiers = results.getIdentifiers();
			for (String identifier : identifiers) {
				Object fact = results.getValue(identifier);
				
				log.info("fact: {}", fact);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
