package com.esko.Utils.CSVFileReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esko.Dao.MasterAssociateDao;
import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Model.Employeedetails;
import com.esko.Service.MasterAssociateService;
import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.JSONObject.ResponseMasterFeedJson;
import com.esko.Utils.Pdf.PropertiesFile;
import com.opencsv.CSVReader;

@Service
public class MasterAssociateFeedReader {
	@Autowired
	MasterAssociateDao masterAssociateDao;
	@Inject
	private MasterAssociateService masterAssociateService;
	private static final String SPLIT_BY_COMMA = ",";
	final static Logger LOGGER = Logger.getLogger(MasterAssociateFeedReader.class);

	public JSONObject read(InputStream uploadedInputStream) throws UnsupportedFileException {
		LOGGER.info("In Masterfeed reader");
		CSVReader cr = null;
		// To store each row of uploaded CSV file
		String row[] = null;
		// to store unsuccessfully parsed lines of the leave feed.
		ArrayList<String[]> unsuccessfulLines = new ArrayList<String[]>();
		// to store successfully parsed lines of the leave feed
		ArrayList<String[]> successfulLines = new ArrayList<String[]>();
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			cr = new CSVReader(new InputStreamReader(uploadedInputStream));
			row = cr.readNext();
			System.out.println("line is: " + row[0]);
			if (row[0] == "") {
				cr.close();
				throw new UnsupportedFileException("Invalid input file format");
			} else {
				System.out.println("not empty");
			}
			// Name of the columns in the uploaded CSV file.
			// value stored for the master feed columns in the spandana
			// properties
			// file is in the same order as expected in the uploaded CSV
			// file
			String[] masterFeedColumns = PropertiesFile.readProperties("spandana.properties").getProperty("MasterFeedColumns")
					.split(SPLIT_BY_COMMA);
			checkColumnValues(masterFeedColumns, row, cr);
			while ((row = cr.readNext()) != null) {
				String[] rowValues = new String[5];
				for (int i = 0; i < 4; i++) {
					rowValues[i] = row[i];
				}
				EmployeeDetails employeeDetails = new EmployeeDetails(rowValues[0].toUpperCase(), rowValues[1], "No",
						rowValues[2],rowValues[3]);
				Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameShowingDeleted(rowValues[0]);
				String reason = "correct";
				if (employeedetails == null) {
					try {
						masterAssociateService.addEmployee(employeeDetails);
						rowValues[4] = reason;
						successfulLines.add(rowValues);
					} catch (NullPointerException e) {
						reason = e.getMessage();
						rowValues[4] = reason;
						unsuccessfulLines.add(rowValues);
					}
				} else {
					masterAssociateService.updateEmployeeDetails(employeeDetails);
					rowValues[4] = reason;
					successfulLines.add(rowValues);
				}

			}
			JSONArray unsuccessfulMasterJSON = ResponseMasterFeedJson.createJSON(unsuccessfulLines);
			JSONArray successfulMasterJSON = ResponseMasterFeedJson.createJSON(successfulLines);
			parsedCSVJSON.put("successfullyParsed", successfulMasterJSON);
			parsedCSVJSON.put("unsuccessfullyParsed", unsuccessfulMasterJSON);
			System.out.println("Sending data to rest API : " + parsedCSVJSON.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			cr.close();
		} catch (IOException e) {
		}
		return parsedCSVJSON;
	}

	/*
	 * to check whether the column values in the uploaded CSV file is in the
	 * same order as in the native feed format expected.
	 */
	private void checkColumnValues(String[] masterFeedColumns, String[] columnValues, CSVReader cr)
			throws UnsupportedFileException {
		for (int i = 0, j = 0; i < columnValues.length && j < masterFeedColumns.length; j++, i++) {
			LOGGER.info("MasterFeedColumn properties is: " + masterFeedColumns[j]);
			LOGGER.info("column values " + columnValues[i]);
			if (!(columnValues[i].equalsIgnoreCase(masterFeedColumns[j]))) {
				try {
					cr.close();
				} catch (IOException e) {
				}
				throw new UnsupportedFileException("Invalid input file format");
			}
		}
	}
}
