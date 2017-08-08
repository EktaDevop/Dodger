package com.esko.Dao;

import java.util.Date;
import java.util.List;

import com.esko.FrontEndObjects.HolidayCalendar;
import com.esko.Model.Holidaycalendar;

public interface HolidayCalenderDao {
	/*
	 * to get the list of holidays
	 */
	public List<Holidaycalendar> getHolidayList();

	/*
	 * Inserting Holiday into table
	 */
	void insertHoliday(Holidaycalendar cal);

	/*
	 * Updating the row in holiday details table
	 */
	void updateCalendarRow(HolidayCalendar cal);

	/*
	 * Deleting holiday row
	 */
	void deleteCalendarRow(Holidaycalendar cal);

	/*
	 * get the Holiday calendar by id
	 */
	public Holidaycalendar getHolidaycalendarById(int id);

	/*
	 * to check holiday on a particular date
	 */
	public boolean checkHoliday(Date in);
}
