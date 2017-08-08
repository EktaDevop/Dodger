package com.esko.Controllers;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Service.MasterAssociateService;
import com.esko.Utils.Exception.OtherException;
import com.google.gson.Gson;

@Component
@Path("/masterController")
public class MasterController {
	final static Logger LOGGER = Logger.getLogger(MasterController.class.getName());
	@Inject
	private MasterAssociateService masterAssociateService;

	/*
	 * Create a rest api to get details of the employees from the database
	 */
	@GET
	@Path("/EmployeeDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getEmployeeDetails(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("in get employee details Controller");
			List<EmployeeDetails> list = masterAssociateService.getEmployeeDetails();
			LOGGER.info("List of employee detail send to UI" + list.toString());
			String json = new Gson().toJson(list);
			return Response.status(201).entity(json).build();
		}

	}

	/*
	 * Create a rest api method to insert rows in the employee details
	 */
	@POST
	@Path("/addEmployee")
	@Consumes("application/json")
	public Response addEmployee(EmployeeDetails employeeDetails, @Context HttpServletRequest request)
			throws OtherException {
		LOGGER.info("In addition employee controller :");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String result = masterAssociateService.addEmployee(employeeDetails);
			return Response.ok(result).status(201).build();
		}
	}

	/*
	 * Create a rest api method to update rows in the employee details
	 */
	@PUT
	@Path("/updateEmployee")
	@Consumes("application/json")
	public Response updateEmployeeDetails(EmployeeDetails[] employeeDetailsRows, @Context HttpServletRequest request)
			throws OtherException {
		LOGGER.info("Updating employee details Controller");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			for (EmployeeDetails employeeDetails : employeeDetailsRows) {
				masterAssociateService.updateEmployeeDetails(employeeDetails);
			}
			return Response.ok("Updated").status(201).build();
		}

	}

	/*
	 * Create a rest api method to delete rows in the employee details
	 */
	@DELETE
	@Path("/deleteEmployee")
	@Consumes("application/json")
	public Response deleteEmployee(EmployeeDetails[] employeeDetails, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("Deleting employee in Controller");
			LOGGER.info("employee in delete" + employeeDetails.toString());
			JSONArray unsuccessful = new JSONArray();
			JSONObject response = new JSONObject();
			String result = "";
			for (EmployeeDetails employeeDetail : employeeDetails) {
				LOGGER.info("Employee in delete" + employeeDetail.toString());
				result = masterAssociateService.deleteEmployee(employeeDetail);
				if (result != "Deleted") {
					unsuccessful.put(employeeDetail.getEmployeeId());
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
	 * Create a rest api method to get list of all the mangers
	 */
	@GET
	@Path("/Manager")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getmanagers(@Context HttpServletRequest request) {
		LOGGER.info("in get manager details Controller");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			List<String> managers = masterAssociateService.getmanagers();
			String json = new Gson().toJson(managers);
			LOGGER.info("List of employee detail send to UI" + managers.toString());
			return Response.status(201).entity(json).build();
		}

	}

}
