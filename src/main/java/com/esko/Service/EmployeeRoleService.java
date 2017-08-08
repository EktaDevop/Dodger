package com.esko.Service;

import com.esko.Model.Employeedetails;

public interface EmployeeRoleService {
	/*
	 * adding role of employee in employee role table
	 */
	public void addEmployeeRoles();

	/*
	 * getting role of the employee by employeedetail
	 */
	public String getEmployeeRole(Employeedetails employeedetails);
}
