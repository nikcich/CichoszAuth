package com.cichosz.anotherone.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import java.util.ArrayList;
import javax.naming.*;

import com.cichosz.anotherone.auth.UserCredentials;
import com.cichosz.anotherone.common.TTLHashMap;
import com.cichosz.anotherone.MyResource;
import com.cichosz.anotherone.auth.*;
import java.util.logging.Logger;

public class MyCache {
	private static MyCache _instance = null;
	
	private TestDBInterface db;
	
    TTLHashMap<String, Object> sessionMapping = new TTLHashMap<>(10, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);
    private static final Logger LOGGER = Logger.getLogger(MyCache.class.getName());
	
	public static MyCache getInstance() {
		if(_instance == null) {
			_instance = new MyCache();
		}
		
		return _instance;
	}
	
	private MyCache() {
		try {
			InitialContext ctx = new InitialContext();
			db = (TestDBInterface) ctx.lookup("java:global/anotherone/TestDBInterface");
		}catch(Exception e) {
			System.out.println(e.toString());
		}finally {
			
		}
	}
	
	public List<Integer> getList(){
		return Collections.unmodifiableList(db.getData());
	}
	
	public void addList() {
		db.insertData();
	}
	
	public String nextUUID() {
		return UUIDGenerator.generateUUID();
	}
	
	public String createUserSession(UserCredentials creds) {
		String sessionId = this.nextUUID();
		UserSession session = new UserSession().withId(sessionId);
		session.setCredentials(creds);
		
		this.sessionMapping.put(sessionId, session, 10, TimeUnit.SECONDS);
		
		return sessionId;
	}
	
	public List<String> getActiveSessions(){
		return new ArrayList<>(this.sessionMapping.keySet());
	}
	
	public String execute(String query) {
		String res = db.execute(query);
		System.out.println(query);
		return res;
	}
	
	public UserSession login(UserCredentials creds) {
		String sessionId = this.nextUUID();
		UserSession sess = new UserSession().withId(sessionId);
		sess.setSuccess(false);
		try {
			HashMap<String, Object> dbInfo = db.getUserByUsername(creds.getUsername());
			
			
			String pw = (String)dbInfo.get("password");
			boolean match = PasswordUtils.checkPassword(creds.getPassword(), pw);
			
			if(dbInfo.keySet().size() > 0 && match) {
				sess.setCredentials(creds);
				sess.setSuccess(true);
			}
		}catch(Exception e) {}
		
		if(sess.getSuccess()) {
			this.sessionMapping.put(sessionId, sess, 60, TimeUnit.SECONDS);
		}
		
		return sess;
	}
	
	public UserSession signup(UserCredentials creds) {
		LOGGER.info("Signing up...");
		
		String sessionId = this.nextUUID();
		UserSession sess = new UserSession().withId(sessionId);
		sess.setSuccess(false);
		try {
			HashMap<String, Object> dbInfo = db.getUserByUsername(creds.getUsername());
			
			String hashedPass = PasswordUtils.hashPassword(creds.getPassword());
			
			LOGGER.info("Hash is " + hashedPass);
			
			if(dbInfo.keySet().size() != 0) {
				LOGGER.info("User does exist");
				sess.setCredentials(creds);
				sess.setSuccess(false);
			}else {
				LOGGER.info("yass");
				boolean succ = db.createUser(creds.getUsername(), hashedPass);
				sess.setSuccess(succ);
			}
		}catch(Exception e) {}
		
		if(sess.getSuccess()) {
			this.sessionMapping.put(sessionId, sess, 60, TimeUnit.SECONDS);
		}
		
		return sess;
	}
}

