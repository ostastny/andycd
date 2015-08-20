package cz.ostastny.andycd.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import io.swagger.annotations.Api;


@Api(tags = {"ReleasePath"})
@Path("ReleasePath")
public class ReleasePath {
	 @POST
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response createPath() {
		 //create new git repo
		 Logger.getLogger(getClass()).info("createPath()");
 		
		 return Response.ok().build();
	 }
	 
	 @POST
	 @Path("release")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response createRelease() {
		 //start new release
		 Logger.getLogger(getClass()).info("createPath()");
	 		
		 return Response.ok().build();
	 }
}
