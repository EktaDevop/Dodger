package com.esko.Utils.Plugin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DataTransformUtil {
	
	/**
	 * to convert the required attribute in the targetFormat
	 * @param enumMap
	 * @throws ParseException
	 */
	public static void transformInputFeed( Map<InputHeaders, String> enumMap) throws ParseException{
		
		String attendanceDate = enumMap.get(InputHeaders.ATTENDANCE_DATE);
		DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yy"); 
		DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = originalFormat.parse(attendanceDate);
		String formattedDate = targetFormat.format(date);
		enumMap.put(InputHeaders.ATTENDANCE_DATE, formattedDate);
		
	}

}
