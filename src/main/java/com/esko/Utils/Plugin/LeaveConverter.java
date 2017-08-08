package com.esko.Utils.Plugin;

import java.io.InputStream;
import java.util.ServiceLoader;

import com.esko.Utils.Exception.UnsupportedFileException;

public abstract class LeaveConverter {
	
	public static LeaveConverter getDefault() {

		   // load our plugin
		   ServiceLoader<LeaveConverter> serviceLoader =
		   ServiceLoader.load(LeaveConverter.class);

		   //checking if load was successful
		   for (LeaveConverter converter : serviceLoader) {
			   return converter;
		   }
		   throw new Error("Something is wrong with registering the addon");
		   }

		   public abstract void convert(InputStream uploadedInputStream,String fileName) throws UnsupportedFileException;
		//   public abstract void convertLeave(InputStream uploadedInputStream);

}
