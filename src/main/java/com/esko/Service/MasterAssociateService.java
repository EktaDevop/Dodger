package com.esko.Service;

import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;

import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Employeedetails;

public interface MasterAssociateService {
	/*
	 * To get the list of departments
	 */
	public List<Departmentdetails> getDepartmentDetails();

	/*
	 * To add department in the Department Details Table
	 */
	public void addDepartmentRow(Departmentdetails departmentDetailsFeed);

	/*
	 * To delete department from the department table
	 */
	public String deleteDepartmentRow(Departmentdetails departmentDetailsFeed);

	/*
	 * get the departmentDetail by department name
	 */
	public Departmentdetails getDepartmentByName(String departmentName);

	/*
	 * checking if the department exists
	 */
	public String checkDepartment(String departmentName);
	
	/*
	 * To get the details of all the employees
	 */
	public List<EmployeeDetails> getEmployeeDetails();

	/*
	 * Getting the list of all the managers
	 */
	public List<String> getmanagers();

	/*
	 * Adding employee in the employee details table and respective manager in
	 * the employee manager table
	 */
	public String addEmployee(EmployeeDetails employeeDetails) throws NullPointerException;

	/*
	 * Updating the details of the employee
	 */
	public void updateEmployeeDetails(EmployeeDetails employeeDetails);

	/*
	 * deleting the employee
	 */
	public String deleteEmployee(EmployeeDetails employeeDetails);

	/*
	 * Validating the uploaded masterFeed CSV and generating json for the
	 * successfully parsed and unsuccessful data
	 */
	public JSONObject read(InputStream uploadedInputStream);

	/*
	 * Getting employee detail by employee name
	 */
	public Employeedetails getEmployeeDetailByName(String employeeId);

	/*
	 * Checking if the employee exist in the spandana database
	 */
	public Employeedetails checkEmployee(String uid);

	/*
	 * getting the manager name of the employee 
	 */
	public String getEmployeeMangerDetails(Employeedetails employeedetails);

	

}
