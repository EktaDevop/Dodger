package com.esko.Utils.CSVFileReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esko.Dao.AttendanceFeedDao;
import com.esko.Dao.HolidayCalenderDao;
import com.esko.Dao.MasterAssociateDao;
import com.esko.Model.Attendancefeed;
import com.esko.Model.AttendancefeedId;
import com.esko.Model.Employeedetails;
import com.esko.Other.ResourcePath;
import com.esko.Utils.CSVFileValidation.AttendanceFeedValidation;
import com.esko.Utils.DepamentTimeStrategy.Context;
import com.esko.Utils.DepamentTimeStrategy.GRC;
import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.JSONObject.ResponseAttendanceFeedJson;
import com.esko.Utils.Pdf.PropertiesFile;
import com.opencsv.CSVReader;

@Service
public class AttendanceFeedReader {
	@Autowired
	AttendanceFeedValidation attendanceFeedValidation;
	@Autowired
	AttendanceFeedDao attendanceFeedDao;
	@Autowired
	MasterAssociateDao masterAssociateDao;
	@Autowired
	private HolidayCalenderDao holidayCalendarDao;

	private static final String SPLIT_BY_COMMA = ",";
	final static Logger LOGGER = Logger.getLogger(AttendanceFeedReader.class.getName());

	/*
	 * To read the input attendance feed CSV and store the data in the database
	 */
	public JSONObject read() throws UnsupportedFileException {
		LOGGER.info("In Attendance Feed reader");
		CSVReader cr = null;
		// To store each row of uploaded CSV file
		String row[] = null;
		// to store unsuccessfully parsed lines of the attendance feed.
		ArrayList<String[]> unsuccessfulLines = new ArrayList<String[]>();
		// to store successfully parsed lines of the attendance feed
		ArrayList<String[]> successfulLines = new ArrayList<String[]>();
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			File file = null;
			ResourcePath resource = new ResourcePath();
			try {
				String resourcePath = resource.getResourcePath();
				file = new File(resourcePath + "/AttendanceFeed.csv");
			} catch (Exception e) {
			}
			cr = new CSVReader(new FileReader(file));
			row = cr.readNext();
			// Name of the columns in the uploaded CSV file.
			// value stored for the attendancefeedcolumns in the spandana
			// properties
			// file is in the same order as expected in the uploaded CSV
			// file
			String[] attendanceFeedColumns = PropertiesFile.readProperties("spandana.properties")
					.getProperty("AttendanceFeedColumns").split(SPLIT_BY_COMMA);
			checkColumnValues(attendanceFeedColumns, row, cr);
			while ((row = cr.readNext()) != null) {
				String[] rowValues = new String[6];
				for (int i = 0; i < 4; i++) {
					rowValues[i] = row[i];
				}
				Date in = null;
				// If day is saturday or sunday ignore it
				try {
					in = getDate(row[1]);
					SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
					String day = simpleDateformat.format(in);
					if ((day.equalsIgnoreCase("Saturday")) || (day.equalsIgnoreCase("Sunday"))) {
						continue;
					}
				} catch (ParseException e) {
					rowValues[4] = "Incorrect Date Format";
					unsuccessfulLines.add(rowValues);
				}
				// if day is a holiday ignore it
				boolean holiday = holidayCalendarDao.checkHoliday(in);
				if (holiday) {
					continue;
				}
				// To Check if employee exists in employee details database
				Employeedetails employeedetails = masterAssociateDao
						.getEmployeeDetailsByNameHidingDeleted(rowValues[0]);
				LOGGER.info("employee details is :" + employeedetails);
				// If the employee does not exist that row will be considered as
				// incorrect and hence will be added to unsuccessful data
				if (employeedetails == null) {
					rowValues[4] = "Employee is not part of esko";
					unsuccessfulLines.add(rowValues);
				} else {
					// Validating if the values of date,attendance type and
					// attendance
					// duration are correct or not
					String[] values = attendanceFeedValidation.checkRow(rowValues);
					for (int i = 0; i < 5; i++) {
						rowValues[i] = values[i];
					}
					// If the check is true i.e the values are correct it will
					// be added into the successful data and row will be added
					// to the database
					if (values[5].equalsIgnoreCase("true")) {
						String status = "Present";
						String dayDuration = "";
						Time checkIn = getCorrectTimeFormat(rowValues[2]);
						Time checkOut = null;
						// To get the date in the format yyyy-mm-dd so
						// that
						// it
						// can be stored in the database
						Date inputDate = getInputdate(rowValues[1]);
						AttendancefeedId attendanceFeedId = new AttendancefeedId();
						Attendancefeed attendanceFeedRow = new Attendancefeed();

						// if user has forgot to logout entry will be marked as
						// absent in the database and will be send as
						// unsuccessful data
						if ((rowValues[3].equalsIgnoreCase("00:00")) || (rowValues[3].equalsIgnoreCase("0:00"))) {
							rowValues[4] = values[4];
							unsuccessfulLines.add(rowValues);
							status = "Absent";
							checkOut = getCorrectTimeFormat("00:00:00");
							attendanceFeedId = new AttendancefeedId(rowValues[0].toUpperCase(), inputDate);

							attendanceFeedRow = new Attendancefeed(attendanceFeedId, employeedetails, checkIn, checkOut,
									"Full", status);
						} else {
							rowValues[4] = values[4];
							checkOut = getCorrectTimeFormat(rowValues[3]);
							Time breakPoint = getCorrectTimeFormat("13:00:00");
							Context context = new Context(new GRC());
							dayDuration = context.executeStrategy(checkIn, checkOut, breakPoint);
							// If the user has both check in and check out and
							// has worked more than 4 hrs then data will be
							// added in successful
							if (dayDuration != "ABSENT") {
								rowValues[4] = values[4];
								successfulLines.add(rowValues);
								attendanceFeedId = new AttendancefeedId(rowValues[0].toUpperCase(), inputDate);

								attendanceFeedRow = new Attendancefeed(attendanceFeedId, employeedetails, checkIn,
										checkOut, dayDuration, status);
							} else {
								rowValues[4] = "Worked less than 4 hrs!";
								unsuccessfulLines.add(rowValues);
								status = "Absent";
								attendanceFeedId = new AttendancefeedId(rowValues[0].toUpperCase(), inputDate);

								attendanceFeedRow = new Attendancefeed(attendanceFeedId, employeedetails, checkIn,
										checkOut, "Full", status);
							}
						}
						
						// to check whether the row exist in the
						// database if
						// it
						// exist it will be created otherwise row will
						// be
						// created in
						// the database
						boolean rowExist = attendanceFeedDao.checkRow(attendanceFeedRow);
						if (rowExist) {
							attendanceFeedDao.updateAttendancefeed(attendanceFeedRow);
						} else {
							attendanceFeedDao.createAttendancefeed(attendanceFeedRow);
						}

					} else {
						unsuccessfulLines.add(rowValues);
					}

				}
			}
			JSONArray unsuccessfulattendanceJSON = ResponseAttendanceFeedJson.createJSON(unsuccessfulLines);
			JSONArray successfulattendanceJSON = ResponseAttendanceFeedJson.createJSON(successfulLines);
			parsedCSVJSON.put("successfullyParsed", successfulattendanceJSON);
			parsedCSVJSON.put("unsuccessfullyParsed", unsuccessfulattendanceJSON);
		} catch (IOException e) {
		}
		try {
			cr.close();
		} catch (IOException e) {

		}
		return parsedCSVJSON;
	}

	private Date getDate(String dateIn) throws ParseException {
		String format = "dd/MM/yyyy";
		DateFormat srcDf = new SimpleDateFormat(format);
		Date date = null;
		date = srcDf.parse(dateIn);
		return date;
	}

	/*
	 * to check whether the column values in the uploaded CSV file is in the
	 * same order as in the native feed format expected.
	 */
	private void checkColumnValues(String[] attendanceFeedColumns, String[] columnValues, CSVReader cr)
			throws UnsupportedFileException {
		for (int i = 0, j = 0; i < columnValues.length && j < attendanceFeedColumns.length; j++, i++) {
			LOGGER.info("attendanceFeedColumn is: " + attendanceFeedColumns[j]);
			LOGGER.info("column values " + columnValues[i]);
			if (!(columnValues[i].equalsIgnoreCase(attendanceFeedColumns[j]))) {
				try {
					cr.close();
				} catch (IOException e) {
				}
				throw new UnsupportedFileException("Invalid input file format");
			}
		}
	}

	/*
	 * To covert the string in the csv to the required time format i.e 24hrs
	 * format
	 */
	private static Time getCorrectTimeFormat(String checkOutInput) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("kk:mm:ss");
		long ms = 0;
		Time checkOut = null;
		try {
			ms = fromFormat.parse(checkOutInput).getTime();
			checkOut = new Time(ms);
		} catch (ParseException e) {
		}
		return checkOut;
	}

	/*
	 * to convert the date in the input attendance feed into the format of date
	 * accepted in the database
	 */
	public static Date getInputdate(String inputDate) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date inDate = null;
		try {
			inDate = fromFormat.parse(inputDate);
		} catch (ParseException e) {
		}
		DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
		String in = toFormat.format(inDate);
		Date outDate = null;
		try {
			outDate = toFormat.parse(in);
		} catch (ParseException e) {
		}
		return outDate;
	}

}
