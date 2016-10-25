package ssa;

import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.jbpm.persistence.jta.ContainerManagedTransactionManager;
import org.jbpm.services.task.persistence.JPATaskPersistenceContextManager;
import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.KnowledgeBaseFactory;

import bitronix.tm.TransactionManagerServices;

public class Redo {

	public static void main(String[] args) {
		try {

			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieBase kbase = kContainer.getKieBase("kbase");

			JBPMHelper.startH2Server();
			JBPMHelper.setupDataSource();
			// EntityManagerFactory emf =
			// Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");

			// Create the entity manager factory and register it in the
			// environment:
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("LocalBPM");

			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);


			// Create a new knowledge session that uses JPA to store the runtime
			// state:
			// StatefulKnowledgeSession ksession =
			// JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null,
			// env);

			// Start the transaction:

			ExecutorService executorService = ExecutorServiceFactory.newExecutorService(emf);

			QueryContext queryContext = new QueryContext();

			List<RequestInfo> allReq = executorService.getAllRequests(queryContext);

			for (RequestInfo reqInfo : allReq) {
				System.out.println(reqInfo.getId() + " - " + reqInfo.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

}
