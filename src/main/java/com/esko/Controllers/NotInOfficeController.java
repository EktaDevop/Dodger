package com.esko.Controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.esko.FrontEndObjects.NotInOfficeHistory;
import com.esko.FrontEndObjects.OutOfOffice;
import com.esko.Model.Employeedetails;
import com.esko.Model.Notinoffice;
import com.esko.Other.DateConverter;
import com.esko.Service.EmployeeRoleService;
import com.esko.Service.MasterAssociateService;
import com.esko.Service.NotInOfficeService;
import com.google.gson.Gson;

@Component
@Path("/notInOfficeController")
public class NotInOfficeController {
	final static Logger LOGGER = Logger.getLogger(NotInOfficeController.class.getName());
	@Inject
	private NotInOfficeService notInOfficeService;
	@Inject
	private MasterAssociateService masterAssociateService;
	@Inject
	private EmployeeRoleService employeeRoleService;

	/*
	 * Getting the user basic info
	 */
	@GET
	@Path("/userDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getUserDetails(@Context HttpServletRequest request) {
		JSONObject jSON = new JSONObject();
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			Employeedetails employeedetails = masterAssociateService.getEmployeeDetailByName(employeeId);
			int wfhEligibleDays = employeedetails.getWfhDays();
			String role = employeeRoleService.getEmployeeRole(employeedetails);
			String employeeName = employeedetails.getFullName();
			String managerId = masterAssociateService.getEmployeeMangerDetails(employeedetails);
			String wfhEligibility = employeedetails.getWfhEligibility();
			Date date = new Date();
			int wfhDays = notInOfficeService.getWfhDays(employeeId, date);
			try {
				jSON.put("employeeId", employeeId);
				jSON.put("employeeName", employeeName);
				jSON.put("managerId", managerId);
				jSON.put("wfhEligibility", wfhEligibility);
				jSON.put("wfhDays", wfhDays);
				jSON.put("wfhEligibleDays", wfhEligibleDays);
				jSON.put("role", role);
			} catch (JSONException e) {
			}
			return Response.status(201).entity(jSON.toString()).build();
		}

	}

	/*
	 * getting the leave list for which user has applied for leaves
	 */
	@GET
	@Path("/leaveDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getLeaveDetails(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			List<Date> listLeaveDates = notInOfficeService.listLeaveDates(employeeId);
			System.out.println("in response :" + listLeaveDates.toString());
			String json = new Gson().toJson(listLeaveDates);
			return Response.status(201).entity(json).build();
		}

	}

	/*
	 * getting the leave list for which user has applied for leaves
	 */
	@GET
	@Path("/workFromHomeList")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getworkFromHomeList(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			List<Date> listWFHDates = notInOfficeService.listWFHDates(employeeId);
			System.out.println("in response :" + listWFHDates.toString());
			String json = new Gson().toJson(listWFHDates);
			return Response.status(201).entity(json).build();
		}

	}

	@POST
	@Path("/addNotInOffice")
	@Consumes("application/json")
	public Response addOutOfOffice(OutOfOffice outOfOffice, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			LOGGER.info("in add Not in office Controller");
			Date dateOfRequestStartTemp = DateConverter.getDateInputString("dd-MM-yyyy",
					outOfOffice.getDateOfRequestStart());
			Date dateOfRequestEndTemp = DateConverter.getDateInputString("dd-MM-yyyy",
					outOfOffice.getDateOfRequestEnd());
			Date dateOfRequestStart = DateConverter.getDateInputDate("yyyy-MM-dd", dateOfRequestStartTemp);
			Date dateOfRequestEnd = DateConverter.getDateInputDate("yyyy-MM-dd", dateOfRequestEndTemp);
			Date date = new Date();
			Date currentDate = DateConverter.getDateInputDate("yyyy-MM-dd", date);
			String employeeId = (String) session.getAttribute("employeeId");
			Employeedetails employeedetails = masterAssociateService.getEmployeeDetailByName(employeeId);
			Employeedetails managerdetails = masterAssociateService.getEmployeeDetailByName(outOfOffice.getManagerId());
			Notinoffice notInOffice = new Notinoffice(employeedetails, managerdetails, dateOfRequestStart,
					outOfOffice.getRemarks(), "Pending", outOfOffice.getRequestType(), dateOfRequestEnd, currentDate);
			String status = notInOfficeService.addNotInOffice(notInOffice);
			if (status.equalsIgnoreCase("Added")) {
				return Response.ok(status).status(201).build();
			} else {
				return Response.ok(status).status(406).build();
			}
		}

	}

	@GET
	@Path("/notInOfficeDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getNotInOfficeHistory(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			List<Notinoffice> list = notInOfficeService.getNotInOfficeHistory(employeeId);
			LOGGER.info("in response :" + list.toString());
			JSONArray notInOfficeList = getNotInOfficeList(list);
			return Response.status(201).entity(notInOfficeList.toString()).build();
		}

	}

	/**
	 * Create a rest api method to cancel applied not in office
	 * 
	 * @param notInOfficeHistory
	 *            nioh : Not in Office History Object
	 * @returns Response
	 */
	@DELETE
	@Path("/cancelRequestUser")
	@Consumes("application/json")
	public Response cancelRequest(NotInOfficeHistory[] nioh, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			for (NotInOfficeHistory nioh1 : nioh) {
				LOGGER.info("not in office request in delete" + nioh1.toString());
				notInOfficeService.cancelRequest(nioh1.getId(), employeeId);
			}
			return Response.ok("result").status(201).build();
		}

	}

	/**
	 * Create a rest api method to cancel applied not in office
	 * 
	 * @param notInOfficeHistory
	 *            nioh : Not in Office History Object
	 * @returns Response
	 */
	@DELETE
	@Path("/cancelRequestManager")
	@Consumes("application/json")
	public Response cancelRequestManager(NotInOfficeHistory[] nioh, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			for (NotInOfficeHistory nioh1 : nioh) {
				LOGGER.info("not in office request in delete" + nioh1.toString());
				notInOfficeService.cancelRequestManager(nioh1.getId(), employeeId);
			}
			return Response.ok("result").status(201).build();
		}

	}

	@GET
	@Path("/pendingRequests")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response pendingRequests(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			String employeeId = (String) session.getAttribute("employeeId");
			List<Notinoffice> list = notInOfficeService.pendingRequests(employeeId);
			LOGGER.info("in response :" + list.toString());
			JSONArray notInOfficeList = getNotInOfficeList(list);
			return Response.status(201).entity(notInOfficeList.toString()).build();
		}

	}

	/**
	 * Create a rest api method to cancel applied not in office
	 * 
	 * @param notInOfficeHistory
	 *            nioh : Not in Office History Object
	 * @returns Response
	 */
	@POST
	@Path("/approveRequest")
	@Consumes("application/json")
	public Response approveRequest(NotInOfficeHistory[] nioh, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			for (NotInOfficeHistory nioh1 : nioh) {
				int id = nioh1.getId();
				LOGGER.info("Not in office request in approve " + nioh1.toString());
				notInOfficeService.approveRequest(id);
			}
			return Response.ok("result").status(201).build();
		}

	}

	private JSONArray getNotInOfficeList(List<Notinoffice> list) {
		JSONArray notInOfficeList = new JSONArray();
		for (Notinoffice notinoffice : list) {
			JSONObject notInOffice = new JSONObject();
			String employeeId = notinoffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId();
			Date from = notinoffice.getDateOfRequestStart();
			Date to = notinoffice.getDateOfRequestEnd();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String startDate = simpleDateFormat.format(from);
			String endDate = simpleDateFormat.format(to);
			String requestType = notinoffice.getRequestType();
			String requestStatus = notinoffice.getRequestStatus();
			String remarks = notinoffice.getRemarks();
			int id = notinoffice.getId();
			try {
				notInOffice.put("id", id);
				notInOffice.put("employeeId", employeeId);
				notInOffice.put("requestType", requestType);
				notInOffice.put("dateOfRequestStart", startDate);
				notInOffice.put("dateOfRequestEnd", endDate);
				notInOffice.put("requestStatus", requestStatus);
				notInOffice.put("remarks", remarks);
			} catch (JSONException e) {
			}
			notInOfficeList.put(notInOffice);
		}
		return notInOfficeList;
	}

}
