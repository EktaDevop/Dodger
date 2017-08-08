package com.esko.Utils.CSVFileReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import com.esko.Dao.HolidayCalenderDao;
import com.esko.Dao.LeaveFeedDao;
import com.esko.Dao.MasterAssociateDao;
import com.esko.Model.Employeedetails;
import com.esko.Model.Leavefeed;
import com.esko.Model.LeavefeedId;
import com.esko.Other.ResourcePath;
import com.esko.Utils.CSVFileValidation.LeaveFeedValidation;
import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.JSONObject.ResponseLeaveFeedJson;
import com.esko.Utils.Pdf.PropertiesFile;
import com.opencsv.CSVReader;

@Service
public class LeaveFeedReader {
	@Autowired
	LeaveFeedValidation leaveFeedValidation;
	@Autowired
	LeaveFeedDao leaveFeedDao;
	@Autowired
	MasterAssociateDao masterAssociateDao;
	@Autowired
	private HolidayCalenderDao holidayCalendarDao;

	private static final String SPLIT_BY_COMMA = ",";
	final static Logger LOGGER = Logger.getLogger(LeaveFeedReader.class.getName());

	/*
	 * To read the input leave feed CSV and store the data in the database
	 */
	public JSONObject read() throws UnsupportedFileException, IOException {
		LOGGER.info("In leavefeed reader");
		CSVReader cr = null;
		// To store each row of uploaded CSV file
		String row[] = null;
		// to store unsuccessfully parsed lines of the leave feed.
		ArrayList<String[]> unsuccessfulLines = new ArrayList<String[]>();
		// to store successfully parsed lines of the leave feed
		ArrayList<String[]> successfulLines = new ArrayList<String[]>();
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			File file = null;
			ResourcePath resource = new ResourcePath();	
			try {
				String resourcePath = resource.getResourcePath();
				file = new File(resourcePath + "/LeaveFeed.csv");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cr = new CSVReader(new FileReader(file));
			row = cr.readNext();
			System.out.println("line is: " + row.toString());
			if (row[0] == "") {
				cr.close();
				throw new UnsupportedFileException("Invalid input file format");
			} else {
				System.out.println("not empty");
			}
			// Name of the columns in the uploaded CSV file.
			// value stored for the leavefeedcolumns in the spandana properties
			// file is in the same order as expected in the uploaded CSV
			// file
			String[] leaveFeedColumns = PropertiesFile.readProperties("spandana.properties").getProperty("LeaveFeedColumns")
					.split(SPLIT_BY_COMMA);
			checkColumnValues(leaveFeedColumns, row, cr);

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
				// To Check if employee exists in employeedetails database
				Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(row[0]);
				LOGGER.info("employee details is :" + employeedetails);
				// If the employee does not exist that row will be considered as
				// incorrect and hence will be added to unsuccessful data
				if (employeedetails == null) {
					rowValues[4] = "Employee is not part of esko";
					unsuccessfulLines.add(rowValues);

				} else {
					String[] values = leaveFeedValidation.checkRow(rowValues);
					for (int i = 0; i < 5; i++) {
						rowValues[i] = values[i];
					}
					// Validating if the values of date,Leave type and leave
					// duration are correct or not
					// If the check is true i.e the values are correct it will
					// be added into the successful data and row will be added
					// to the database
					if (values[5].equalsIgnoreCase("true")) {
						successfulLines.add(rowValues);
						// To get the date in the format yyyy-mm-dd so that it
						// can be stored in the database
						Date inputDate = getInputDate(row[1]);
						LeavefeedId leaveFeedId = new LeavefeedId(row[0],inputDate,row[3]);
						Leavefeed leaveFeedRow = new Leavefeed(leaveFeedId, employeedetails, row[2]);

						// to check whether the row exist in the database if it
						// exist it will be created otherwise row will be
						// created in
						// the database
						boolean rowExist = leaveFeedDao.checkRow(leaveFeedRow);
						if (rowExist) {
							leaveFeedDao.updateLeaveFeed(leaveFeedRow);
						} else {
							leaveFeedDao.createLeaveFeed(leaveFeedRow);
						}

					} else {
						unsuccessfulLines.add(rowValues);
					}

				}
			}
			JSONArray unsuccessfulLeaveJSON = ResponseLeaveFeedJson.createJSON(unsuccessfulLines);
			JSONArray successfulLeaveJSON = ResponseLeaveFeedJson.createJSON(successfulLines);
			parsedCSVJSON.put("successfullyParsed", successfulLeaveJSON);
			parsedCSVJSON.put("unsuccessfullyParsed", unsuccessfulLeaveJSON);
		} catch (IOException e) {
		}
		cr.close();
		return parsedCSVJSON;
	}

	/*
	 * to check whether the column values in the uploaded CSV file is in the
	 * same order as in the native feed format expected.
	 */
	private void checkColumnValues(String[] leaveFeedColumns, String[] columnValues, CSVReader cr)
			throws UnsupportedFileException {
		for (int i = 0, j = 0; i < columnValues.length && j < leaveFeedColumns.length; j++, i++) {
			LOGGER.info("LeaveFeedColumn is: " + leaveFeedColumns[j]);
			LOGGER.info("column values " + columnValues[i]);
			if (!(columnValues[i].equalsIgnoreCase(leaveFeedColumns[j]))) {
				try {
					cr.close();
				} catch (IOException e) {
				}
				throw new UnsupportedFileException("Invalid input file format");
			}
		}
	}

	/*
	 * to convert the date in the input leave feed into the format of date
	 * accepted in the database
	 */
	public Date getInputDate(String inputDate) {
		SimpleDateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date inDate = null;
		Date outDate = null;
		try {
			inDate = fromFormat.parse(inputDate);
			String in = toFormat.format(inDate);
			outDate = toFormat.parse(in);
		} catch (ParseException e) {
		}
		return outDate;
	}

	private Date getDate(String dateIn) throws ParseException {
		String format = "dd/MM/yyyy";
		DateFormat srcDf = new SimpleDateFormat(format);
		Date date = null;
			date = srcDf.parse(dateIn);
		return date;
	} 
	
}
