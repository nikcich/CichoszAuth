package com.cichosz.auth.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.cichosz.auth.services.AuthServiceCache;

public class ConfigReader {
    private Properties properties;
    
    private static ConfigReader _instance = null;

    public ConfigReader() {
        properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/config.properties");
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key, String defaultV) {
    	String value = properties.getProperty(key);
        return (value != null) ? value : defaultV;
    }
    
    public static ConfigReader getInstance() {
		if(_instance == null) {
			_instance = new ConfigReader();
		}
		
		return _instance;
	}
}

