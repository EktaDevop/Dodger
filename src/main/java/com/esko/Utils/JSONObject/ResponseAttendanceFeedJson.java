package com.esko.Utils.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.esko.Utils.Pdf.PropertiesFile;

public class ResponseAttendanceFeedJson {
	final static Logger LOGGER = Logger.getLogger(ResponseAttendanceFeedJson.class.getName());
	/*
	 * Creates the CSV files for the successfully parsed attendance feed data
	 */
	private static final String SPLIT_BY_COMMA = ",";

	public static JSONArray createJSON(ArrayList<String[]> rows) {
		JSONArray attendanceFeedJson = new JSONArray();
		for (String[] values : rows) {
			JSONObject row = new JSONObject();
			String[] attendanceFeedColumns = PropertiesFile.readProperties("spandana.properties").getProperty("AttendanceFeedColumns")
					.split(SPLIT_BY_COMMA);
			for (int i = 0, j = 0; i < values.length && j < attendanceFeedColumns.length; i++, j++) {
				try {
					row.put(attendanceFeedColumns[j], values[i]);
				} catch (JSONException e) {
				}
			}
			try {
				row.put("Reason", values[4]);
			} catch (JSONException e) {
			}
			attendanceFeedJson.put(row);
		}
		LOGGER.info("json Array is: " + attendanceFeedJson.toString());
		return attendanceFeedJson;
	}

}
