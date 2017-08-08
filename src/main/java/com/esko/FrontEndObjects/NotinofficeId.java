package com.esko.FrontEndObjects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to add id to not in office in report 
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotinofficeId {

	Date dateOfRequestStart;
	String employeeId;

}
