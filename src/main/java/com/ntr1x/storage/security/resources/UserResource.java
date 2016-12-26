package com.ntr1x.storage.security.resources;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.ntr1x.storage.core.transport.PageResponse;
import com.ntr1x.storage.core.transport.PageableQuery;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.services.IUserService;
import com.ntr1x.storage.security.services.IUserService.CreateUser;
import com.ntr1x.storage.security.services.IUserService.UpdateUser;

import io.swagger.annotations.Api;


@Api("Users")
@Component
@Path("/users")
@PermitAll
public class UserResource {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private IUserService users;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ "res:///users:admin" })
    public PageResponse<User> list(
    	@BeanParam PageableQuery pageable
    ) {
        return new PageResponse<>(
        	users.query(pageable.toPageRequest())
		);
    }

    @GET
    @Path("/i/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ "res:///users/i/{id}:admin" })
    public User select(@PathParam("id") long id) {
        
        return users.select(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public User create(CreateUser user) {

        return users.create(user);
	}

	@PUT
	@Path("/i/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public User update(@PathParam("id") long id, UpdateUser user) {
	    
	    return users.update(id, user);
	}
	
	@DELETE
    @Path("/i/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ "res:///users/i/{id}:admin" })
    public User remove(@PathParam("id") long id) {
        
	    return users.remove(id);
    }
}
