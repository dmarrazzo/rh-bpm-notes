package ssa.simple_case;

import org.kie.api.runtime.process.ProcessContext;

public class SimpleTest {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProcessContext kcontext = null;

		// ---------------------------------
		org.jbpm.casemgmt.CaseMgmtService cmService = new org.jbpm.casemgmt.CaseMgmtUtil(kcontext);

		java.util.Map<String, Object> workParams = new java.util.HashMap<String, Object>();
		workParams.put("Message", "");
		
		long processInstanceId = kcontext.getProcessInstance().getId();
		cmService.createDynamicWorkTask(processInstanceId, "Log", workParams);
		String[] adHocFragmentNames = cmService.getAdHocFragmentNames(processInstanceId);
		for (String adHoc : adHocFragmentNames) {
			System.out.println("adHoc: "+adHoc);
			cmService.triggerAdHocFragment(processInstanceId, adHoc);
		}
		

		// ---------------------------------
		System.out.println("Entering in node: " + kcontext.getNodeInstance().getNodeName());
		//RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
		
	}

}
