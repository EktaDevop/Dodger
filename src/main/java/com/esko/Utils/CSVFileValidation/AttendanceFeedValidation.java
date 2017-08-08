package com.esko.Utils.CSVFileValidation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esko.Dao.MasterAssociateDao;

@Service
public class AttendanceFeedValidation {
	@Autowired
	MasterAssociateDao masterAssociateDao;

	final static Logger LOGGER = Logger.getLogger(AttendanceFeedValidation.class.getName());

	/*
	 * Validating each values in the attendance feed row if they are in the
	 * correct format
	 */
	public String[] checkRow(String[] values) {
		LOGGER.info("Checking row Attendance");
		boolean checkDateFormat = validateDateFormat(values[1]);
		boolean checkCheckInFormat = validateTimeFormat(values[2]);
		boolean checkCheckOutFormat = validateTimeFormat(values[3]);
		boolean checkDateDuration = validateDateDuration(values[1]);
		LOGGER.info("Date duration is :" + checkDateDuration + " Date format is: " + checkDateFormat
				+ " Check-in time is: " + checkCheckInFormat + " Check-out time is:" + checkCheckOutFormat);
		if(checkDateFormat == false){
			values[4]="Invalid Date Format!";
			values[5]="false";
			return values;
		}else if(checkCheckInFormat == false){
			values[4]="Absent";
			values[5]="false";
			return values;
		}else if(checkDateDuration == false){
			values[4]="1 year old data is accepted !";
			values[5]="false";
			return values;
		}else if(checkCheckOutFormat == false){
			values[4]="Forgot to log out";
			values[5]="true";
			return values;
		}else{
			values[4]="Data correct!";
			values[5]="true";
			return values;
		}
	}

	/*
	 * validating if the checkOut and checkIn time is in hh:mm:ss
	 */
	private static boolean validateTimeFormat(String checkOut) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("kk:mm:ss");
		long ms = 0;
		try {
			ms = fromFormat.parse(checkOut).getTime();
			return true;
		} catch (ParseException e) {
		}
		return false;
	}

	/*
	 * validating if the inputdate is not more than 1 year old
	 */
	private static boolean validateDateDuration(String inputDate) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date currentDate = new Date();
		String currentDateFormated = fromFormat.format(currentDate);
		try {
			Date currentDateFinal = fromFormat.parse(currentDateFormated);
			Date inputDateFinal = fromFormat.parse(inputDate);
			long differenceDays = getDateDifference(currentDateFinal, inputDateFinal);
			if (differenceDays > 364) {
				return false;
			} else
				return true;
		} catch (ParseException e) {
		}
		return false;
	}

	/*
	 * to validate if the date is in dd/mm/yyyy format
	 */
	private static boolean validateDateFormat(String inputDate) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date inDate = null;
		try {
			inDate = fromFormat.parse(inputDate);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/*
	 * to get the difference between two dates
	 */
	public static long getDateDifference(Date currentDateFinal, Date inputDateFinal) {
		long difference = currentDateFinal.getTime() - inputDateFinal.getTime();
		long diffenceDays = difference / (24 * 60 * 60 * 1000);
		return diffenceDays;
	}
}
