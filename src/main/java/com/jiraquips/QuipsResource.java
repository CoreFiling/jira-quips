package com.jiraquips;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.opensymphony.user.User;

import java.util.Map;
import java.util.Collection;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericEntity;

/**
 * A resource of message.
 */
@Path("/quips")
public class QuipsResource {

	public QuipsResource(JiraAuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}
	
	JiraAuthenticationContext authenticationContext;
	
    /**
     * This method will be called if no extra path information
     * is used in the request.
     * @param key optional key for the message
     * @return the message from the key parameter or the default message
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    public Response getQuip(@QueryParam("key") String key)
    {
		if (key != null) {
            return Response.ok(getQuipFromKey(key)).build();
		}
        else {
			// Get *all* quips
			QuipList rtn = new QuipList(new ArrayList<Quip>(new QuipsCollection().getQuips().values()));
			return Response.ok(rtn).build();
		}
    }

	@POST
	@Path("/")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes("application/x-www-form-urlencoded")
	public Response addQuip(@FormParam("quip") String text) {
		final User author = authenticationContext.getUser();
		Quip newQuip = new QuipsCollection().addQuip(author, text);
		return Response.ok(newQuip).build();
	}
	
    /**
     * This method will be called if the request contains an extra path
     * element.  The extra path element is extracted and passed in as the
     * key parameter.
     * @param key the key of the message
     * @return the message defined by the key
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{key}")
    public Response getQuipFromPath(@PathParam("key") String key)
    {
        return Response.ok(getQuipFromKey(key)).build();
    }

    private Quip getQuipFromKey(String key) {
		Map quips = new QuipsCollection().getQuips();
		if (key.equals("random")) {
			Random generator = new Random();
			return (Quip)quips.values().toArray()[generator.nextInt(quips.size())];
		}
		else {
			return (Quip)quips.get(key);
		}
    }
}
