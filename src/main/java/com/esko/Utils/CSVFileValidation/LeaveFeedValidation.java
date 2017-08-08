package com.esko.Utils.CSVFileValidation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.esko.Utils.Pdf.PropertiesFile;

@Service
public class LeaveFeedValidation {
	final static Logger LOGGER = Logger.getLogger(LeaveFeedValidation.class.getName());
	private static final String SPLIT_BY_COMMA = ",";

	/*
	 * Validating each row in the uploaded leave feed file if they are in the
	 * correct format
	 */
	public String[] checkRow(String[] values) {
		LOGGER.info("Validating the values of rows in leave feed");
		boolean checkDateFormat = validateDateFormat(values[1]);
		boolean checkLeaveType = validateLeaveType(values[2]);
		boolean checkLeaveDuration = validateDuration(values[3]);
		boolean checkDateDuration = validateDateDuration(values[1]);
		LOGGER.info(" Date format is: " + checkDateFormat + " Leave Type is: " + checkLeaveType + " Leave Duration is:"
				+ checkLeaveDuration + "Date duration is: " + checkDateDuration);
		if(checkDateFormat == false){
			values[4]="Invalid Date Format!";
			values[5]="false";
			return values;
		}else if(checkLeaveType == false){
			values[4]="Leave type is incorrect!";
			values[5]="false";
			return values;
		}else if(checkLeaveDuration == false){
			values[4]="Leave duration type is incorrect!";
			values[5]="false";
			return values;
		}else if(checkDateDuration == false){
			values[4]="Date duration is more than a year!";
			values[5]="false";
			return values;
		}else{
			values[4]="Data correct!";
			values[5]="true";
			return values;
		}
	}

	/*
	 * validating if the inputdate is not more than 1 year old
	 */
	private boolean validateDateDuration(String inputDate) {
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
	 * validate if the duration for the leave is acceptable value i.e the values
	 * in duration enum
	 */
	private boolean validateDuration(String duration) {
		String[] DurationType = null;
		DurationType = PropertiesFile.readProperties("spandana.properties").getProperty("DurationType").split(SPLIT_BY_COMMA);
		for (String durationType : DurationType) {
			if (durationType.equalsIgnoreCase(duration)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * validate if the type for the leave is acceptable value i.e the values in
	 * leavetype enum
	 */
	private boolean validateLeaveType(String leave_type) {
		String[] LeaveType = null;
		LeaveType = PropertiesFile.readProperties("spandana.properties").getProperty("LeaveType").split(SPLIT_BY_COMMA);
		for (String leaveType : LeaveType) {
			if (leaveType.equalsIgnoreCase(leave_type)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * to validate if the date is in dd/mm/yyyy format
	 */
	private boolean validateDateFormat(String inputDate) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date inDate = fromFormat.parse(inputDate);
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
