package com.cichosz.anotherone.auth;

import java.util.HashMap;

public class UserSession {

	private UserCredentials credentials;
	private HashMap<String, Object> info = new HashMap<>();
	private String id;
	private boolean success=false;
	
	public void setSuccess(boolean b) {
		this.success = b;
	}
	
	public boolean getSuccess() {
		return this.success;
	}
	
	public void setCredentials(UserCredentials c) {
		this.credentials = c;
	}
	
	public UserCredentials getCredentials() {
		return this.credentials;
	}
	
	public void offerInfo(String key, Object v) {
		this.info.put(key,v);
	}
	
	public Object findInfo(String key) {
		return this.info.get(key);
	}
	
	public String getId() {
		return this.id;
	}
	
	public UserSession withId(String s) {
		this.id = s;
		return this;
	}
}
