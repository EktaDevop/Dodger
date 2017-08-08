package com.esko.Utils.Exception;

@SuppressWarnings("serial")
public class ParseException extends Exception{
	public ParseException(Throwable e) {
		super(e);
	}

	public ParseException(String message) {
		super(message);
	}

}
