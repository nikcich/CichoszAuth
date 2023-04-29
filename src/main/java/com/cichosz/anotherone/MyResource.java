package com.cichosz.anotherone;

import com.cichosz.anotherone.auth.UserCredentials;
import com.cichosz.anotherone.auth.UserSession;
import com.cichosz.anotherone.services.*;
import java.util.List;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("myresource")
public class MyResource {
    private static final Logger LOGGER = Logger.getLogger(MyResource.class.getName());
    
	private MyCache cache;
	
	@PostConstruct
	public void init() {
		cache = MyCache.getInstance();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Hello";
	}
	
	@Path("/json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> yas() {
		return cache.getList();
	}
	
	@Path("/add")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String yep() {
		cache.addList();
		return "Adding...";
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
	
	@Path("/post")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> urmom(HashMap<String, Object> things){
		
		return things;
	}
	
	@Path("/runQuery")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public String urmom(String data){
		
		String res = cache.execute(data);
		
		return res;
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
	
}
