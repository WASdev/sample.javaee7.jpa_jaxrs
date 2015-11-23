/**
* (C) Copyright IBM Corporation 2015.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.test.service.rest;

import java.net.URI;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.test.service.builders.MinionServiceHelper;
import com.test.service.model.Minion;

/**
 * Restful MinionService
 */
@Path("/Minion")
public class MinionService extends Application {
	private MinionServiceHelper serviceHelper;
	
    @Context
    UriInfo uriInfo;
    
    /**
     * Constructor
     */
    public MinionService() {
        super();
        serviceHelper = new MinionServiceHelper();
    }

	@Produces(MediaType.APPLICATION_JSON)	
	@GET
	/** 
	 * Retrieve a minion from the database, 
	 * Example syntax to call, and how it's built
	 * 
	 *   J2EEService is configured in ibm-web-ext.xml
	 *   1.0 is configured in web.xml
	 *   Minion is configured in this class (see class name)
	 *   
	 * http://localhost:9080/J2EEService/1.0/Minion/{name}
	 * @return the HTTP response, including the minion contents
	 */
	public Response get(@PathParam("name") String name) {
		Response res;
		
		try {
			List<Minion> minions = serviceHelper.get(name);
		    URI uri = uriInfo.getAbsolutePathBuilder().build();
			res = Response.ok(minions).header("Location", uri).build();
		}
		catch (NotFoundException nfe) {
			nfe.printStackTrace();
			res = Response.status(HttpServletResponse.SC_NOT_FOUND).entity(nfe.getMessage()).build();  
		}
		catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			res = Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(iae.getMessage()).build();
		}
		catch (PersistenceException pe) {
			pe.printStackTrace();
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(pe.getMessage()).build();
		}
		catch (Throwable t) {
			t.printStackTrace();
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
		}
		
		return res;
	}
	
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Create a new minion 
	 * Example URL to call:
	 * POST to http://localhost:9080/J2EEService/1.0/Minion
	 * Attach a payload with at least two parameters, "name" and "parentName", for example:
	 * {
   	 *	  "name" : "test minion",
   	 *	  "parentName": "test parent"
	 * }
	 * @return the HTTP response
	 */
	public Response create(Minion minion) {
		Response res = null;
		
		try {
			minion = serviceHelper.persist(minion);
		
			URI uri = uriInfo.getAbsolutePathBuilder().path(minion.getName()).build();
		    res = Response.ok(minion).header("Location", uri).build();
		}
		catch (NotFoundException nfe) {
			res = Response.status(HttpServletResponse.SC_NOT_FOUND).entity(nfe.getMessage()).build();  
		}
		catch (IllegalArgumentException iae) {
			res = Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(iae.getMessage()).build();
		}
		catch (PersistenceException pe) {
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(pe.getMessage()).build();
		}
		catch (Throwable t) {
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
		}
		
		return res;
	}

	@DELETE
	/** Example URL to call:
	 * DELETE to http://localhost:9080/J2EEService/1.0/Minion/
	 * @return the HTTP response
	 */
	public Response delete() {
		Response res;
		
		try {
			serviceHelper.delete(null);

			res = Response.noContent().build();
		}
		catch (NotFoundException nfe) {
			res = Response.status(HttpServletResponse.SC_NOT_FOUND).entity(nfe.getMessage()).build();  
		}
		catch (PersistenceException pe) {
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(pe.getMessage()).build();
		}
		catch (Throwable t) {
			res = Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
		}
		
		return res;
	}
}
