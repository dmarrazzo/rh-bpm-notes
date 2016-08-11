package ssa.rest;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;

/**
 * Business Central URL http://localhost:8080/business-central/rest/operas
 * 
 * @author donato
 *
 */
@RequestScoped
@Path("/operas")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class OperaEndpoint {

	private static final String GROUP_ID = "SSA";
	private static final String ARTIFACT_ID = "Versioning";
	private static final String VERSION = "1.1";
	private static final String PROCESS_NAME = "Versioning.VersionProc";

	@Inject
	private DeploymentService deploymentService;

	@Inject
	private ProcessService processService;

	@POST
	public Response create(final Opera opera) {
		System.out.println("OperaEndpoint.create() " + opera.getId());

		DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
		Collection<DeployedUnit> units = deploymentService.getDeployedUnits();
		for (DeployedUnit unit : units) {
			System.out.println("OperaEndpoint.create() " + unit.getDeploymentUnit().getIdentifier());
		}

		processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_NAME);

		return Response
				.created(UriBuilder.fromResource(OperaEndpoint.class).path(String.valueOf(opera.getId())).build())
				.build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") final Long id) {

		System.out.println("OperaEndpoint.findById() " + id);
		Opera opera = new Opera();
		opera.setId(1);
		opera.setAuthor("donato");
		// if (opera == null) {
		// return Response.status(Status.NOT_FOUND).build();
		// }
		return Response.ok(opera).build();
	}

}
