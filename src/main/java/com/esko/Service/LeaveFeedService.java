package com.esko.Service;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.esko.Model.Leavefeed;

public interface LeaveFeedService {

	/*
	 * Validating the uploaded leaveFeed CSV and generating json for the
	 * successfully parsed and unsuccessful data
	 */
	public JSONObject read();

	/*
	 * get the details of the data present in the leaveFeed between selected
	 * date range
	 */
	public List<Leavefeed> listLeaveFeed(Date startDate, Date endDate);
}
