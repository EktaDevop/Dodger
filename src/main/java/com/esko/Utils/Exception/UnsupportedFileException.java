package com.esko.Utils.Exception;

@SuppressWarnings("serial")
public class UnsupportedFileException extends Exception {

	public UnsupportedFileException(Throwable e) {
		super(e);
	}

	public UnsupportedFileException(String message) {
		super(message);
	}

}
