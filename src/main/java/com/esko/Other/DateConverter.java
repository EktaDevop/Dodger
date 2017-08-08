package com.esko.Other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateConverter {
	public static Date getDateInputDate (String format,Date currDate){
		DateFormat dateFormat = new SimpleDateFormat(format);
		String dateString = dateFormat.format(currDate);
		Date date = new Date();
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateInputString (String format,String currDate){
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date inDate = null;
		try {
			inDate = dateFormat.parse(currDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return inDate;
	}
	
	/*
	 * getting the date range for which report has to be generated
	 */
	public static List<Date> getDateRange(Date startDate, int dayDifference) {
		List<Date> dateRangeList = new ArrayList<Date>();
		if (dayDifference > 1) {
			for (int i = 1; i <= dayDifference; i++) {
				dateRangeList.add(startDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				startDate = getDateInputDate("yyyy-MM-dd",cal.getTime());
			}
		} else {
			dateRangeList.add(startDate);
		}
		return dateRangeList;
	}
}
