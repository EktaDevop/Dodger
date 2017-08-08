package com.esko.FrontEndObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to send and receive the out of office detail 
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotInOfficeHistory {
	
	private int id;
	private String employeeId;
	private String requestType;
	private String dateOfRequestStart;
	private String dateOfRequestEnd;
	private String requestStatus;
	private String remarks;
}
