package com.esko.Utils.Exception;

@SuppressWarnings("serial")
public class IOException extends Exception{
	public IOException(Throwable e) {
		super(e);
	}

	public IOException(String message) {
		super(message);
	}

}
