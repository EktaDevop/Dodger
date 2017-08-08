package com.esko.FrontEndObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to send and receive the employee details 
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDetails {

	private String employeeId;

	private String employeeName;

	private String wfhEligibility;

	private String managerId;

	private String departmentId;

}
