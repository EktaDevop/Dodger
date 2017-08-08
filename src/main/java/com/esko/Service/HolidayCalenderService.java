package com.esko.Service;

import java.util.List;

import com.esko.FrontEndObjects.HolidayCalendar;
import com.esko.Model.Holidaycalendar;

public interface HolidayCalenderService {
	/*
	 * to get the list of holidays
	 */
	public List<Holidaycalendar> getHolidayList();

	/*
	 * Inserting Holiday into table
	 */
	public void insertHoliday(Holidaycalendar cal);

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

}
