package com.cichosz.anotherone;

import com.cichosz.anotherone.services.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
    private static final Logger LOGGER = Logger.getLogger(MyResource.class.getName());

	/**
	 * Method handling HTTP GET requests. The returned object will be sent
	 * to the client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
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
	
}
