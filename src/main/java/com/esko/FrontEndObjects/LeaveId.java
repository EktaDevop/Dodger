package com.esko.FrontEndObjects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to check leave applied
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeaveId {
	String employeeId;
	Date leaveDate;
}


