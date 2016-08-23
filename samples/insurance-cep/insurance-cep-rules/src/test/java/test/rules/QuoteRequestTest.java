package test.rules;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.ssa.util.Event;

import insurance.cep.model.Buy;
import insurance.cep.model.Customer;
import insurance.cep.model.CustomerLevel;
import insurance.cep.model.QuoteRequest;

public class QuoteRequestTest {

	private static KieSession kieSession;

	private static final Logger log = LoggerFactory.getLogger(QuoteRequestTest.class);

	public static void test() {
		kieSession = getKieSession();
		PseudoClockScheduler clock = kieSession.getSessionClock();
		Buy buy = new Buy("", 0, 0);
		// Facts
		Customer c1 = new Customer("100", CustomerLevel.BASIC, "BB111XX");
		kieSession.insert(c1);
		Customer c2 = new Customer("200", CustomerLevel.GOLDEN, "GG222ZZ");
		kieSession.insert(c2);

		// Events
		Date now = new Date();
		QuoteRequest qr;

		clock.setStartupTime(now.getTime());
		qr = new QuoteRequest("BB111XX", 1200, clock.getCurrentTime());
		kieSession.insert(qr);
		kieSession.fireAllRules();

		clock.advanceTime(20, TimeUnit.MINUTES);
		qr = new QuoteRequest("BB111XX", 820, clock.getCurrentTime());
		kieSession.insert(qr);
		kieSession.fireAllRules();

		// To trigger the 3rd rule:
		//clock.advanceTime(15, TimeUnit.MINUTES);
		clock.advanceTime(2, TimeUnit.DAYS);
		qr = new QuoteRequest("BB111XX", 780, clock.getCurrentTime());
		kieSession.insert(qr);
		kieSession.fireAllRules();

		clock.advanceTime(120, TimeUnit.MINUTES);
		// To trigger the 2nd rule:
		//qr = new QuoteRequest("GG222ZZ", 1400, clock.getCurrentTime());
		qr = new QuoteRequest("GG222ZZ", 400, clock.getCurrentTime());
		kieSession.insert(qr);
		kieSession.fireAllRules();

		clock.advanceTime(10, TimeUnit.DAYS);
		kieSession.fireAllRules();

	}

	private static synchronized KieSession getKieSession() {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		KieSession kieSession = kieContainer.newKieSession();

		return kieSession;
	}

	public static void main(String[] args) {
		test();
	}

}
