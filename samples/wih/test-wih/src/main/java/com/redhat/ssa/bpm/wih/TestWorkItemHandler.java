package com.redhat.ssa.bpm.wih;

import java.util.Map;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWorkItemHandler implements WorkItemHandler {
	public final static String WORKITEMHANDLER_NAME = "TEST_WIH";

	public final static String INPUT_PARAM_NAME_WORK = "Work";
	public final static String OUTPUT_PARAM_NAME_RESULT = "Result";

	private Logger log = LoggerFactory.getLogger(getClass());

	private static int count = 4;
	private static boolean executed = true;

	public TestWorkItemHandler() {
		System.out.println("TestWorkItemHandler.TestWorkItemHandler()");
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> output = null;
		System.out.println("TestWorkItemHandler.executeWorkItem() - workitem id: "+workItem.getId());
		log.trace("{} start", WORKITEMHANDLER_NAME);
		
		Map<String, Object> parameters = workItem.getParameters();
		Object countInt = parameters.get("count");
		if (countInt != null && countInt instanceof Integer && executed) {
			count = (Integer)countInt;
			log.trace("count parameter: {}", countInt);
		}
		
		if (count-- == 0) {
			count = 0;
			log.trace("{} completeWIH", WORKITEMHANDLER_NAME);
			System.out.println("TestWorkItemHandler.executeWorkItem() - complete");
			manager.completeWorkItem(workItem.getId(), output);
			executed = true;
		} else {
			log.trace("{} exception WIH", WORKITEMHANDLER_NAME);
			System.out.println("TestWorkItemHandler.executeWorkItem() - exception");
			manager.abortWorkItem(workItem.getId());
			
			executed = false;
			throw new WorkItemHandlerNotFoundException("TestException", workItem.getName());
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		log.trace("{} abortWIH", WORKITEMHANDLER_NAME);
		manager.abortWorkItem(workItem.getId());
	}

}
