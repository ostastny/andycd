package cz.ostastny.andycd.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cz.ostastny.andycd.SimpleOAuthService;
import cz.ostastny.andycd.models.GitHubUser;
import cz.ostastny.andycd.models.ReleasePath;
import io.swagger.annotations.Api;


@Api(tags = {"ReleasePath"})
@Path("ReleasePath")
public class ReleasePathResource {
	 private final Logger log = Logger.getLogger(getClass());
	 
	 @Context
	 private ServletContext servletContext;
	 
	 @Inject Session session;
		
	 
	 @GET
	 @Path("test")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response test() throws URISyntaxException
	 { 
		 // check access token
		 if (SimpleOAuthService.getAccessToken() == null) {
			 return githubAuthRedirect();
		 }
		 
		 // We have already an access token. Query the user info from GitHub
		 final Client client = SimpleOAuthService.getFlow().getAuthorizedClient(); 
	     client.register(JacksonFeature.class);
	        
		 WebTarget target = client.target("https://api.github.com").path("user");

		 Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		 GitHubUser user = resp.readEntity(GitHubUser.class);

		 return Response.ok(user).build();
	 }	 
		
	 public Response githubAuthRedirect() throws URISyntaxException {
		 String clientKey = servletContext.getInitParameter("GitHub.ClientId");
		 String clientSecret = servletContext.getInitParameter("GitHub.ClientSecret");

		 ClientIdentifier clientId = new ClientIdentifier(clientKey, clientSecret);

		 OAuth2CodeGrantFlow.Builder builder =
				 OAuth2ClientSupport.authorizationCodeGrantFlowBuilder(clientId,
						 "https://github.com/login/oauth/authorize",
						 "https://github.com/login/oauth/access_token");
		 
		 final URI redirectUri = new URI("http://localhost:8080/andycd/api/users/authorize");

		 final OAuth2CodeGrantFlow flow = builder
				 .scope("user")
				 .redirectUri(redirectUri.toString())
				 .build();

		 SimpleOAuthService.setFlow(flow);

		 // start the flow
		 final String githubAuthURI = flow.start();
		 	 

		 //redirect user to GitHub
		 return Response.seeOther(new URI(githubAuthURI)).build();
	 }
	 
	 @GET
	 @Produces(MediaType.APPLICATION_JSON)
	 public List<ReleasePath> getAll() {
		 List<ReleasePath> pipelines = null; 

		 pipelines = session.createCriteria(ReleasePath.class).list();
	    	
	     return pipelines;
	 }
	 
	 @POST
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response createPath(ReleasePath rp) {
		 //create new git repo
		 log.info("createPath()");
		 
		 Transaction tx = null;
		 try {
			 tx = session.beginTransaction();

			 if(rp.getId() != null)
			 {
				 ReleasePath rpCopy = (ReleasePath) session.merge(rp);
				 session.saveOrUpdate(rpCopy);
			 }
			 else
			 {
				 session.save(rp);
			 }

			 tx.commit();
		 }catch(org.hibernate.exception.ConstraintViolationException ex) {
			 Logger.getLogger(getClass()).error("DB error", ex);
			 //throw new AppException(409, 0, ex.getMessage(), ex.getSQLException().getMessage(), null);	//Conflict
			 throw ex;
		 }finally {
			 if(tx.isActive())
				 tx.rollback();
		 }
 		
		 return Response.ok().build();
	 }
	 
	 @POST
	 @Path("release")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response createRelease(String commit) {
		 //start new release
		 log.info("createPath() msg: " + commit);
	 		
		 return Response.ok().build();
	 }
}
