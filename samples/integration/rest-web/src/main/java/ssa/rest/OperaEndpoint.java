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

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KnowledgeBaseFactory;

@RequestScoped
@Path("/operas")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class OperaEndpoint {

	@POST
	public Response create(final Opera opera) {
		System.out.println("OperaEndpoint.create() "+opera.getId());
		
		// Internal
		KieBase kieBase = KnowledgeBaseFactory.newKnowledgeBase();
		
		KieSession kieSession = kieBase.newKieSession();
		ProcessInstance processInstance = kieSession.startProcess("VersionProc");
		
		
		return Response
				.created(UriBuilder.fromResource(OperaEndpoint.class).path(String.valueOf(opera.getId())).build())
				.build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") final Long id) {

		System.out.println("OperaEndpoint.findById() "+ id);
		Opera opera = new Opera();
		opera.setId(1);
		opera.setAuthor("donato");
//		if (opera == null) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
		return Response.ok(opera).build();
	}

}
