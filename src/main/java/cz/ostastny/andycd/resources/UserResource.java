package cz.ostastny.andycd.resources;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;

import cz.ostastny.andycd.SimpleOAuthService;
import io.swagger.annotations.Api;

@Api(tags = {"Users"})
@Path("users")
public class UserResource{
	@Context 
	private UriInfo uriInfo;
	
	@GET
    @Path("authorize")
    public Response authorize(@QueryParam("code") String code, @QueryParam("state") String state, @QueryParam("error") String error, @QueryParam("error_description") String description) {
        if(error != null)
        	return Response.serverError().entity(description).build();
        
        
		final OAuth2CodeGrantFlow flow = SimpleOAuthService.getFlow();

        final TokenResult tokenResult = flow.finish(code, state);

        SimpleOAuthService.setAccessToken(tokenResult.getAccessToken());

        // authorization is finished -> now redirect back to the task resource
        final URI uri = UriBuilder.fromUri(uriInfo.getBaseUri()).path("ReleasePath").build();
        return Response.seeOther(uri).build();
    }
}