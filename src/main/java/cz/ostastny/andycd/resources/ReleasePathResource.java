package cz.ostastny.andycd.resources;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import cz.ostastny.andycd.models.ReleasePath;
import io.swagger.annotations.Api;


@Api(tags = {"ReleasePath"})
@Path("ReleasePath")
public class ReleasePathResource {
	 private final Logger log = Logger.getLogger(getClass());
	 
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
