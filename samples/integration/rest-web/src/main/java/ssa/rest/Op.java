package ssa.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/op")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class Op {

	@GET
	public Response test() {
		//TODO: retrieve the opera 
		System.out.println("Op.test()");
		return Response.ok().build();
	}

}
