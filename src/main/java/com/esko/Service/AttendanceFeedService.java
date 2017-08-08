package com.esko.Service;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.esko.Model.Attendancefeed;

public interface AttendanceFeedService {
	/*
	 * Validating the uploaded attendanceFeed CSV and generating json for the
	 * successfully parsed and unsuccessful data
	 */
	public JSONObject read();

	/*
	 * get the details of the data present in the attendance between selected
	 * date range
	 */
	public List<Attendancefeed> listAttendanceFeed(Date startDate, Date endDate);

}
