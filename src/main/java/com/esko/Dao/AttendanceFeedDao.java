package com.esko.Dao;

import java.util.Date;
import java.util.List;

import com.esko.Model.Attendancefeed;

public interface AttendanceFeedDao {
	/*
	 * to check if the same row exists in the database
	 * 
	 * @see
	 * com.esko.Dao.AttendancefeedDao#checkRow(com.esko.Model.Attendancefeed)
	 */
	public boolean checkRow(Attendancefeed AttendancefeedRow);

	/*
	 * creates row in the database for the attendance feed
	 * 
	 * @see com.esko.Dao.AttendancefeedDao#createAttendancefeed(com.esko.Model.
	 * Attendancefeed)
	 */
	public void createAttendancefeed(Attendancefeed Attendancefeed1);

	/*
	 * to update the row in the database if the same row is present in the feed
	 * 
	 * @see com.esko.Dao.AttendancefeedDao#updateAttendancefeed(java.sql.Time,
	 * java.sql.Time, com.esko.Model.Attendancefeed)
	 */
	public void updateAttendancefeed(Attendancefeed AttendancefeedRow);

	/*
	 * to show all the row present in the attendance feed datadbase
	 * 
	 * @see com.esko.Dao.AttendancefeedDao#getAttendancefeed()
	 */
	public List<Attendancefeed> listAttendanceFeed(Date StarDate, Date endDate);

}
