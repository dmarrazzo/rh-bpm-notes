package ssa.rest;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;

import com.thoughtworks.xstream.XStream;

/**
 * Business Central URL
 * http://localhost:8080/business-central/rest/createDynamicWorkTask
 * 
 * @author donato
 *
 */
@ApplicationScoped

@Path("/createDynamicWorkTask")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class Remediation {
	@Inject	
	private RuntimeDataService runtimeData;
		
	@Inject
	private DeploymentService deploymentService;
	
	@PUT
	@Path("/{id:[0-9][0-9]*}/{taskName}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void createDynamicWorkTask(@PathParam("id") long processInstanceId, @PathParam("taskName") String taskName, String params) {
		String deploymentId = runtimeData.getProcessInstanceById(processInstanceId).getDeploymentId();
		
		RuntimeManager manager = deploymentService.getRuntimeManager(deploymentId);
		RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());

		CaseMgmtService cmService = new CaseMgmtUtil(runtime);

		ClassLoader classLoader = getContainerClassloader(deploymentId);
		
		XStream xStream = new XStream();
		xStream.setClassLoader(classLoader);

		@SuppressWarnings("unchecked")
		Map<String, Object> workParams = (Map<String, Object>) xStream.fromXML(params);
		
		cmService.createDynamicWorkTask(processInstanceId, taskName, workParams);

		manager.disposeRuntimeEngine(runtime);

	}

	private ClassLoader getContainerClassloader(String deploymentId) {
		KieServices ks = KieServices.Factory.get();
		
		String[] didTokens = deploymentId.split(":");
		ReleaseId releaseId = ks.newReleaseId(didTokens[0], didTokens[1], didTokens[2]);
		KieContainer kieContainer = ks.newKieContainer(releaseId);
		
		ClassLoader classLoader = kieContainer.getClassLoader();
		return classLoader;
	}

}
