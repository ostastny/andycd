package cz.ostastny.andycd.resources;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

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
	 
	 @GET
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
	 
	 @POST
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response createPath(ReleasePath rp) {
		 //create new git repo
		 log.info("createPath()");
		 
		 String repoName = rp.getName();
		 try
		 {
			 Process p = Runtime.getRuntime().exec("git init " + repoName);
			 p.waitFor();
			 
			 BufferedReader reader = 
					 new BufferedReader(new InputStreamReader(p.getInputStream()));

			 StringBuilder sb = new StringBuilder();	
			 String line = null;		
			 while ((line = reader.readLine())!= null) {
				 sb.append(line + "\n");
			 }
			 
			 PrintWriter out = new PrintWriter(
					    new OutputStreamWriter(
					       new FileOutputStream(repoName + "/.git/hooks/post-commit"), "UTF-8"));
			 
			 out.println("#!/bin/sh");
			 out.println("git log --pretty=format:'%h' -n 1  | curl -X POST --header \"Content-Type: application/json\" --header \"Accept: application/json\" -d @- \"http://localhost:8080/andycd/api/ReleasePath/release\"");
			 out.println("exit 0");
			 out.close();
			 
			 log.info(sb.toString());
		 }
		 catch(Exception ex)
		 {
			 log.error("Git init error", ex);
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
