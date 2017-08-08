package com.esko.Utils.JSONObject;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.esko.Utils.Pdf.PropertiesFile;

public class ResponseMasterFeedJson {
	/*
	 * Creates the JSON Object for the parsed leave feed data
	 */
	private static final String SPLIT_BY_COMMA = ",";
	final static Logger LOGGER = Logger.getLogger(ResponseLeaveFeedJson.class.getName());

	public static JSONArray createJSON(ArrayList<String[]> rows) {
		LOGGER.info("Creating json to send to the UI ");
		JSONArray masterFeedJson = new JSONArray();
		for (String[] values : rows) {
			JSONObject row = new JSONObject();
			String[] masterFeedColumns = PropertiesFile.readProperties("spandana.properties").getProperty("MasterFeedColumns")
					.split(SPLIT_BY_COMMA);
			for (int i = 0, j = 0; i < values.length && j < masterFeedColumns.length; i++, j++) {
				try {
					row.put(masterFeedColumns[j], values[i]);
				} catch (JSONException e) {
				}

			}
			try {
				row.put("Reason", values[4]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			masterFeedJson.put(row);
		}
		LOGGER.info("json Array is: " + masterFeedJson.toString());
		return masterFeedJson;
	}
}
