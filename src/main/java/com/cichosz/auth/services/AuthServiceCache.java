package com.cichosz.auth.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import java.util.ArrayList;
import javax.naming.*;

import com.cichosz.auth.AuthRestService;
import com.cichosz.auth.auth.*;
import com.cichosz.auth.common.ConfigReader;
import com.cichosz.auth.common.TTLHashMap;

import java.util.logging.Logger;

public class AuthServiceCache {
	private static AuthServiceCache _instance = null;
	
	private AuthServiceDBInterface db;
	
	private static final int TEN_MINUTES_IN_MS = Integer.parseInt(ConfigReader.getInstance().getProperty("session.ttl", "600000"));
	private static final int CLEANUP_INTERVAL_MS = Integer.parseInt(ConfigReader.getInstance().getProperty("session.cleanup", "10000"));;
    TTLHashMap<String, Object> sessionMapping = new TTLHashMap<>(TEN_MINUTES_IN_MS, TimeUnit.MILLISECONDS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
    private static final Logger LOGGER = Logger.getLogger(AuthServiceCache.class.getName());
	
	public static AuthServiceCache getInstance() {
		if(_instance == null) {
			_instance = new AuthServiceCache();
		}
		
		return _instance;
	}
	
	private AuthServiceCache() {
		try {
			InitialContext ctx = new InitialContext();
			db = (AuthServiceDBInterface) ctx.lookup("java:global/CichoszAuth/AuthServiceDBInterface");
		}catch(Exception e) {
			System.out.println(e.toString());
		}finally {}
	}
	
	public String nextUUID() {
		return UUIDGenerator.generateUUID();
	}
	
	public String createUserSession(UserCredentials creds) {
		String sessionId = this.nextUUID();
		UserSession session = new UserSession().withId(sessionId);
		
		session.setCredentials(creds);
		this.sessionMapping.put(sessionId, session, TEN_MINUTES_IN_MS, TimeUnit.MILLISECONDS);
		
		return sessionId;
	}
	
	public List<String> getActiveSessions(){
		return new ArrayList<>(this.sessionMapping.keySet());
	}
	
	public String execute(String query) {
		String res = db.execute(query);
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
				sess.setUserId((int)dbInfo.get("id"));
				LOGGER.info(Integer.toString((int)dbInfo.get("id")));
			}
		}catch(Exception e) {}
		
		if(sess.getSuccess()) {
			this.sessionMapping.put(sessionId, sess, TEN_MINUTES_IN_MS, TimeUnit.MILLISECONDS);
		}
		
		return sess;
	}
	
	public UserSession signup(UserCredentials creds) {
		String sessionId = this.nextUUID();
		UserSession sess = new UserSession().withId(sessionId);
		sess.setSuccess(false);
		
		try {
			HashMap<String, Object> dbInfo = db.getUserByUsername(creds.getUsername());
			String hashedPass = PasswordUtils.hashPassword(creds.getPassword());
			
			if(dbInfo.keySet().size() != 0) {
				sess.setCredentials(creds);
				sess.setSuccess(false);
			}else {
				HashMap<String, Object> res = db.createUser(creds.getUsername(), hashedPass);
				boolean succ = (boolean)res.get("success");
				sess.setSuccess(succ);
				sess.setUserId((int)res.get("id"));
			}
		}catch(Exception e) {}
		
		if(sess.getSuccess()) {
			this.sessionMapping.put(sessionId, sess, TEN_MINUTES_IN_MS, TimeUnit.MILLISECONDS);
		}
		
		return sess;
	}

}

