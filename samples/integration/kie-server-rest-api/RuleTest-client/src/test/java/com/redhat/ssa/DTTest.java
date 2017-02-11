package com.redhat.ssa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import ssa.Transaction;

public class DTTest {

	private static KieContainer kieContainer;
	private KieSession kSession;

	@Before
	public void setUp() throws Exception {
		kSession = getKieContainer().getKieBase("RuleTest").newKieSession();

	}

	@After
	public void tearDown() throws Exception {
		kSession.dispose();
	}

	@Test
	public void test() {
		Transaction transaction = new Transaction();
		transaction.setAmount(1.0);
		transaction.setInfo("ok");
		kSession.insert(transaction);
		
		int rulesfired = kSession.fireAllRules();
		
		System.out.println(rulesfired + "\n" + transaction);
		
	}

	private static synchronized KieContainer getKieContainer() {
		if (kieContainer == null) {
			KieServices kieServices = KieServices.Factory.get();
			kieContainer = kieServices.getKieClasspathContainer();
		}

		return kieContainer;
	}

}
