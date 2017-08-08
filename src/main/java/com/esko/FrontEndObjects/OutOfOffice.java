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
public class OutOfOffice {

	private String managerId;
	private String dateOfRequestStart;
	private String dateOfRequestEnd;
	private String requestType;
	private String remarks;

}
