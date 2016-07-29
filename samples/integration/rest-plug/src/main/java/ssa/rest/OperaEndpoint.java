package ssa.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * Business Central URL
 * http://localhost:8080/business-central/rest/operas
 * @author donato
 *
 */
@RequestScoped
@Path("/operas")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class OperaEndpoint {

	@POST
	public Response create(final Opera opera) {
		System.out.println("OperaEndpoint.create() " + opera.getId());

		KieServices kieServices = KieServices.Factory.get();
		KieRepository repository = kieServices.getRepository();
		// group:artifact:version
		ReleaseId releaseId = new ReleaseIdImpl("SSA:Versioning:1.1");
		KieContainer kieContainer = kieServices.newKieContainer(releaseId);
		KieSession kieSession = kieContainer.newKieSession();
		ProcessInstance processInstance = kieSession.startProcess("Versioning.VersionProc");	
		
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
