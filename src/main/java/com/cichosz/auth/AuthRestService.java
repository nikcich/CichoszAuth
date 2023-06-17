package com.cichosz.auth;

import com.cichosz.auth.auth.UserCredentials;
import com.cichosz.auth.auth.UserSession;
import com.cichosz.auth.common.ConfigReader;
import com.cichosz.auth.services.*;

import java.util.List;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/*
 * Root resource (exposed at "myresource" endpoint)
 * E.g.http://localhost:8080/CichoszAuth/auth/service
 * 
 */

@Path("service")
public class AuthRestService {

	private AuthServiceCache cache;
	private ConfigReader confReader;
	
	@PostConstruct
	public void init() {
		cache = AuthServiceCache.getInstance();
		confReader = ConfigReader.getInstance();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Cichosz Basic Auth Service";
	}
	

	@Path("/activeSessions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> activeSessionsGetter() {
		return cache.getActiveSessions();
	}
	
	@Path("/createSession")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String createSession() {
		UserCredentials creds = new UserCredentials();
		creds.setUsername("Usr");
		creds.setPassword("Pw");
		return cache.createUserSession(creds);
	}
	
	@Path("/session")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public String sessionInit(UserCredentials creds){
		return cache.createUserSession(creds);
	}
	
	@Path("/signup")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(UserCredentials creds) {

		UserSession sess = cache.signup(creds);
		
		if (!sess.getSuccess()) {
			// return 401 Unauthorized response
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("{\"error\": \"Invalid username or password\"}")
					.build();
		}
		
		HashMap<String, Object> res = new HashMap<>();

		res.put("session", sess.getId());
		res.put("success", true);

		return Response.ok(res).build();
	}

	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(UserCredentials creds) {
	    // check if the credentials are valid
	    
		UserSession sess = cache.login(creds);
	    
	    if (!sess.getSuccess()) {
	        // return 401 Unauthorized response
	        return Response.status(Response.Status.UNAUTHORIZED)
	                .entity("{\"error\": \"Invalid username or password\"}")
	                .build();
	    }
	    
	    HashMap<String, Object> res = new HashMap<>();
	    
	    res.put("session", sess.getId());
	    res.put("success", true);

	    // otherwise, return a success response
	    return Response.ok(res).build();
	}
	
	@Path("/validate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveRecording(@HeaderParam("Session") String session) {
		boolean success = cache.validateSession(session);
		
		if(success) {
			return Response.ok().build();
		}else {
			return Response.status(Status.UNAUTHORIZED)
	                   .entity("{\"error\":\"" + "Unable To Validate" + "\"}")
	                   .type(MediaType.APPLICATION_JSON)
	                   .build();
		}
	}
}
