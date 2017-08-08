package com.esko.Dao;

import com.esko.Model.Employeedetails;
import com.esko.Model.Employeerole;
import com.esko.Model.Rolemaster;

public interface EmployeeRoleDao {
	/*
	 * adding role of employee in employee role table
	 */
	public void addEmployeeRoles(Employeerole er);

	/* 
	 * getting the role id of the employee by role name
	 */
	public Rolemaster getRoleByName(String employeeName);

	/*
	 * getting role of the employee by employeedetail
	 */
	public String getEmployeeRole(Employeedetails employeedetails);
}
