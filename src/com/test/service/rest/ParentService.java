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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.test.service.builders.ParentServiceHelper;
import com.test.service.model.Parent;

/**
 * Restful JPA Service example
 */
@Path("/Parent")
public class ParentService extends Application {
	private ParentServiceHelper serviceHelper;
	
    @Context
    UriInfo uriInfo;
    
    public ParentService() {
        super();
        serviceHelper = new ParentServiceHelper();
    }

	@Produces(MediaType.APPLICATION_JSON)	
	@GET
	/** 
	 * Example syntax to call, and how it's built
	 * 
	 *   J2EEService is configured in ibm-web-ext.xml
	 *   1.0 is configured in web.xml
	 *   Parent is configured in this class using the @Path parameter
	 *   
	 *   There are no other parameters, so this will get all objects of type Parent
	 *   
	 * http://localhost:9080/J2EEService/1.0/Parent/
	 * @return Response HTTP response with the contents of all the Parent objects
	 */
	public Response get() {
		Response res;
		
		try {
			List<Parent> parents = serviceHelper.get(null);
		    URI uri = uriInfo.getAbsolutePathBuilder().build();
			res = Response.ok(parents).header("Location", uri).build();
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
		
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	/** Example URL to call:
	  * POST to http://localhost:9080/J2EEService/1.0/Parent
	  * Attach a payload with at least one parameter, "name", for example:
	  * {
   	  *   "name" : "test parent"
	  * }
	  * @return Response HTTP response
	  */
	public Response create(Parent parent) {
		Response res = null;
				
		try {
			serviceHelper.persist(parent);
		    URI uri = uriInfo.getAbsolutePathBuilder().path(parent.getName()).build();
		    res = Response.ok(parent).header("Location", uri).build();
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
	
	@PUT
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	/** Example URL to call:
	 * PUT http://localhost:9080/J2EEService/1.0/Parent/{name}
	 * 
	 * @param name the name of the parent to update
	 * 
	 * Attach a payload with the contents to update
	 * 
	 * @return Response The HTTP response
	 */
	public Response update(@PathParam("name") String name, Parent parent)  {
		Response res;
		
		try {
			Parent fullParent = serviceHelper.update(name, parent);
			res = Response.ok(fullParent).build();		
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
	 * DELETE to http://localhost:9080/J2EEService/1.0/Parent/
	 * No payload required
	 * @return Response The HTTP response
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
