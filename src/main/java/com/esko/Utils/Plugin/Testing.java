package com.esko.Utils.Plugin;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import com.esko.Utils.Exception.UnsupportedFileException;

public class Testing {
	private static final String INPUT_FILE_NAME = "C:\\Users\\ekma\\Desktop\\Feeds\\EmployeeAttendanceLogs.csv";
	
	public static void main(String[] args){
		
		Converter con = new ConverterImpl();
		InputStream ir  = null;
		try {
			ir = new FileInputStream(INPUT_FILE_NAME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ir);
		try {
			con.convert(ir);
		} catch (UnsupportedFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
