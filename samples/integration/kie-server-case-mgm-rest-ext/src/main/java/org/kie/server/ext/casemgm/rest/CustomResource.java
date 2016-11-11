package org.kie.server.ext.casemgm.rest;

import static org.kie.server.remote.rest.common.util.RestUtils.createResponse;
import static org.kie.server.remote.rest.common.util.RestUtils.getContentType;
import static org.kie.server.remote.rest.common.util.RestUtils.getVariant;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.services.api.KieContainerInstance;
import org.kie.server.services.api.KieServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("server/containers/{container-id}/processes/instances/{process-instance-id}")
public class CustomResource {

	private static final Logger logger = LoggerFactory.getLogger(CustomResource.class);

	private KieServerRegistry registry;
	private ProcessService processService;
	private DeploymentService deploymentService;
	private RuntimeDataService runtimeDataService;

	/**
	 * Start a dynamic task on a process instance (defined as 'case')
	 * 
	 * URL: <hostname>:<port>/kie-server/services/rest/server/containers/<container-name>/processes/instances/<instance-name>/dynamic-workitem/<task-name>
	 * Method: POST
	 * E.g. payload:
     *
     * {
     *  "Url" : "http://localhost:8090/rest",
     *  "Method" : "GET",
     *  "Content" : {
     *        "payload": {
     *             "model.Payload": {
     *                    "field1": "f1",
     *                    "field2": null,
     *                    "field3": null
     *             }
     *          }
     *     }
     * }
	 * 
	 * @param headers
	 * @param containerId
	 * @param processInstanceId
	 * @param taskName
	 * @param cmdPayload
	 * @return
	 */
	
	@POST
	@Path("/dynamic-workitem/{task-name}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response insertFireReturn(@Context HttpHeaders headers, @PathParam("container-id") String containerId,
			@PathParam("process-instance-id") long processInstanceId, @PathParam("task-name") String taskName,
			String cmdPayload) {

		Variant v = getVariant(headers);
		String contentType = getContentType(headers);

		MarshallingFormat format = MarshallingFormat.fromType(contentType);
		if (format == null) {
			format = MarshallingFormat.valueOf(contentType);
		}
		try {
			KieContainerInstance kci = registry.getContainer(containerId);
			
			Marshaller marshaller = kci.getMarshaller(format);

			@SuppressWarnings("unchecked")
			Map<String, Object> taskParams = (Map<String, Object>) marshaller.unmarshall(cmdPayload, Map.class);
	      
			String deploymentId = runtimeDataService.getProcessInstanceById(processInstanceId).getDeploymentId();
			RuntimeManager runtimeManager = deploymentService.getDeployedUnit(deploymentId).getRuntimeManager();
			RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());

			CaseMgmtService cmService = new CaseMgmtUtil(runtimeEngine);
			cmService.createDynamicWorkTask(processInstanceId, taskName, taskParams);
			
			String result = "";

			return createResponse(result, v, Response.Status.OK);

		} catch (Exception e) {
			// in case marshalling failed return the call container response to
			// keep backward compatibility
			String response = "Execution failed with error : " + e.getMessage();
			logger.debug("Returning Failure response with content '{}'", response);
			return createResponse(response, v, Response.Status.INTERNAL_SERVER_ERROR);
		}

	}


	public KieServerRegistry getRegistry() {
		return registry;
	}


	public void setRegistry(KieServerRegistry registry) {
		this.registry = registry;
	}


	public ProcessService getProcessService() {
		return processService;
	}


	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}


	public DeploymentService getDeploymentService() {
		return deploymentService;
	}


	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}


	public RuntimeDataService getRuntimeDataService() {
		return runtimeDataService;
	}


	public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
		this.runtimeDataService = runtimeDataService;
	}
}
