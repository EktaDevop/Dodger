package com.esko.Dao;

import java.util.Date;
import java.util.List;

import com.esko.Model.Leavefeed;

public interface LeaveFeedDao {
	/*
	 * to check if the same row exists in the database
	 */
	public boolean checkRow(Leavefeed leaveFeedRow);

	/*
	 * creates row in the database for the leave feed
	 */
	public void createLeaveFeed(Leavefeed leaveFeed1);

	/*
	 * to update the row in the database if the same row is present in the feed
	 */
	public void updateLeaveFeed(Leavefeed leaveFeedRow);

	/*
	 * get the details of the data present in the leaveFeed between selected
	 * date range
	 */
	public List<Leavefeed> listLeaveFeed(Date startDate, Date endDate);

}
