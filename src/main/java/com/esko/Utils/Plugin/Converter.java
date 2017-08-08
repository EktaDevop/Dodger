package com.esko.Utils.Plugin;

import java.io.InputStream;
import java.util.ServiceLoader;

import com.esko.Utils.Exception.UnsupportedFileException;

public abstract class Converter {
	public static Converter getDefault() {

		   // load our plugin
		   ServiceLoader<Converter> serviceLoader =
		   ServiceLoader.load(Converter.class);

		   //checking if load was successful
		   for (Converter converter : serviceLoader) {
		   return converter;
		   }
		   throw new Error("Something is wrong with registering the addon");
		   }
			/*
			 * Converting the attendance system file into native feed format of spandana
			 */
		   public abstract void convert(InputStream uploadedInputStream) throws UnsupportedFileException;

		  
}
