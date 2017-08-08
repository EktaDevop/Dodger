package com.esko.Utils.Exception;

@SuppressWarnings("serial")
public class NullPointerException extends Exception{
	public NullPointerException(Throwable e) {
		super(e);
	}

	public NullPointerException(String message) {
		super(message);
	}
}
