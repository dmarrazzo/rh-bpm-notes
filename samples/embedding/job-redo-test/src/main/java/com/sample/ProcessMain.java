package com.sample;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessMain {

	public static void main(String[] args) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");

		RuntimeManager manager = createRuntimeManager(kbase);
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
		try {
			//launchProcess(ksession);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			manager.disposeRuntimeEngine(engine);
			System.exit(0);
		}
	}

	private static void launchProcess(KieSession ksession) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("test", "exception");
		ProcessInstance processInstance = ksession.startProcess("job-redo.ExceptionProc", parameters);

		System.out.println("id: " + processInstance.getId());
		/*--------------
			TaskService taskService = engine.getTaskService();
		
			// let john execute Task 1
			List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
			TaskSummary task = list.get(0);
			System.out.println("John is executing task " + task.getName());
			taskService.start(task.getId(), "john");
			taskService.complete(task.getId(), "john", null);
		
			// let mary execute Task 2
			list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
			task = list.get(0);
			System.out.println("Mary is executing task " + task.getName());
			taskService.start(task.getId(), "mary");
			taskService.complete(task.getId(), "mary", null);
		--------------*/
	}

	private static RuntimeManager createRuntimeManager(KieBase kbase) {
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		//org.jbpm.domain
		//EntityManagerFactory emf = Persistence.createEntityManagerFactory("LocalBPM");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
				.entityManagerFactory(emf).knowledgeBase(kbase);
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get(), "com.sample:example:1.0");

	}

}