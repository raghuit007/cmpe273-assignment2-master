package edu.sjsu.cmpe.procurement.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;



	@Path("/orders")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public class ProcurementResource {
        private  Client restapiClient;
        
        public ProcurementResource(Client client) {
                this.restapiClient = client;
        }
        
    	@GET
    	@Timed(name = "get-root")
    	public Response getRoot() {
        
	
	        return Response.ok().build();
	    }

	}
