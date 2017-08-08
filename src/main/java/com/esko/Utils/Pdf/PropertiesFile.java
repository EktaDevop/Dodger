package com.esko.Utils.Pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/*
 * 
 */
public class PropertiesFile {

	public PropertiesFile() {
	}
	 public static Properties readProperties(String name)  {
	        InputStream inputStream = null;
	        Properties properties = new Properties();
	        try {
	        	ClassLoader classLoader = PropertiesFile.class.getClassLoader();
	        	URL url = classLoader.getResource(name);
			    File file = null;
				try {
					file = new File(url.toURI());
				} catch (URISyntaxException e) {
				}
	            try {
					inputStream = new FileInputStream(file);
				} catch (FileNotFoundException e) {
				}
	            try {
					properties.load(inputStream);
				} catch (IOException e) {
				}
	        } finally {
	            if (inputStream != null) {
	                try {
						inputStream.close();
					} catch (IOException e) {
					}
	            }
	        }
	        return properties;
	    }

}
