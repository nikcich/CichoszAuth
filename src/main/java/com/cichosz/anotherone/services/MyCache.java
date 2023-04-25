package com.cichosz.anotherone.services;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import java.util.ArrayList;

import javax.naming.*;
import java.util.logging.Logger;

public class MyCache {
	private static MyCache _instance = null;
	
	private TestDBInterface db;
	
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
	
	public String execute(String query) {
		String res = db.execute(query);
		System.out.println(query);
		return res;
	}
}

