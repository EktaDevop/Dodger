package com.esko.Controllers;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import com.esko.FrontEndObjects.Department;
import com.esko.Model.Departmentdetails;
import com.esko.Service.MasterAssociateService;

@Component
@Path("/departmentController")
public class DepartmentController {
	final static Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());

	@Inject
	private MasterAssociateService masterAssociateService;

	/*
	 * Creates a rest api to get the list of department
	 */
	@GET
	@Path("/DepartmentDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getDepartmentFeed(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("in get department details controller");
			List<Departmentdetails> list = masterAssociateService.getDepartmentDetails();
			JSONArray departmentList = new JSONArray();
			departmentList = getDepartmentList(list);
			LOGGER.info("department send to the UI  " + departmentList.toString());
			return Response.status(201).entity(departmentList.toString()).build();
		}

	}

	/*
	 * Create a rest api method to insert rows in the department table
	 */
	@POST
	@Path("/addDepartmentRow")
	@Consumes("application/json")
	public Response addDepartmentRow(Department department, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("In addition department Controller ");
			String departmentExists = masterAssociateService.checkDepartment(department.getDepartmentName());
			String result = "";
			if (departmentExists.equalsIgnoreCase("No")) {
				Departmentdetails departmentDetails = new Departmentdetails(department.getDepartmentName());
				masterAssociateService.addDepartmentRow(departmentDetails);
				result = "Added";
			} else {
				result = "Department already exists";
			}

			return Response.ok(result).status(201).build();
		}

	}

	/*
	 * Creates a rest api to delete a department row
	 */
	@DELETE
	@Path("/deleteDepartmentRow")
	@Consumes("application/json")
	public Response deleteDepartmentRow(Department[] departments, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("In Deletion department Controller ");
			JSONArray unsuccessful = new JSONArray();
			JSONObject response = new JSONObject();
			String result = "";
			for (Department department : departments) {
				Departmentdetails departmentDetails = masterAssociateService
						.getDepartmentByName(department.getDepartmentName());
				LOGGER.info("department in delete" + departmentDetails.toString());
				result = masterAssociateService.deleteDepartmentRow(departmentDetails);
				if (result != "Deleted") {
					unsuccessful.put(department.getDepartmentName());
				}
			}
			try {
				response.put("unsuccessful", unsuccessful);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return Response.ok(response.toString()).status(201).build();
		}

	}

	/*
	 * to get the department Name list from the department details table as we
	 * only need department name in the UI
	 */
	private JSONArray getDepartmentList(List<Departmentdetails> list) {
		JSONArray departmentList = new JSONArray();
		String departmentName = "";
		for (Departmentdetails departmentDetails1 : list) {
			JSONObject department = new JSONObject();
			departmentName = departmentDetails1.getDepartmentName();
			try {
				department.put("departmentName", departmentName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			departmentList.put(department);
		}
		return departmentList;
	}

}
