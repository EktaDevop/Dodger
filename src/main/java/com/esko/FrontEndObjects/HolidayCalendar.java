package com.esko.FrontEndObjects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to send and receive the holiday calendar 
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayCalendar {
	
	int id;
	Date holidayDate;
	String holiday;
	String department;
}
