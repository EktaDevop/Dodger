package com.esko.FrontEndObjects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to send and receive the report 
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AbsentDetails {
	private String employeeID;
	private String managerID;
	private String department;
	private Date absentRecordDate;
	private String day;
	private String checkIn;
	private String checkOut;
	private String remarks;
}
