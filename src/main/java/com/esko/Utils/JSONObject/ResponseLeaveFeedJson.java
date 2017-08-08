package com.esko.Utils.JSONObject;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.esko.Utils.Pdf.PropertiesFile;

public class ResponseLeaveFeedJson {
	/*
	 * Creates the JSON Object for the parsed leave feed data
	 */
	private static final String SPLIT_BY_COMMA = ",";
	final static Logger LOGGER = Logger.getLogger(ResponseLeaveFeedJson.class.getName());

	public static JSONArray createJSON(ArrayList<String[]> rows) {
		LOGGER.info("Creating json to send to the UI ");
		JSONArray leaveFeedJson = new JSONArray();
		for (String[] values : rows) {
			JSONObject row = new JSONObject();
			String[] leaveFeedColumns = PropertiesFile.readProperties("spandana.properties").getProperty("LeaveFeedColumns")
					.split(SPLIT_BY_COMMA);
			for (int i = 0, j = 0; i < values.length && j < leaveFeedColumns.length; i++, j++) {
				try {
					row.put(leaveFeedColumns[j], values[i]);
				} catch (JSONException e) {
				}
			}
			try {
				row.put("Reason", values[4]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			leaveFeedJson.put(row);
		}
		return leaveFeedJson;
	}

}
