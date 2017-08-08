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

import com.esko.FrontEndObjects.HolidayCalendar;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Holidaycalendar;
import com.esko.Service.HolidayCalenderService;
import com.esko.Service.MasterAssociateService;

@Component
@Path("/holidayController")
public class HolidayController {
	final static Logger LOGGER = Logger.getLogger(HolidayController.class.getName());
	@Inject
	private HolidayCalenderService holidayCalenderService;

	@Inject
	private MasterAssociateService masterAssociateService;

	/**
	 * Create a rest api method to get details of the holidays from database
	 * 
	 * @returns Response
	 */
	@GET
	@Path("/holidayDetails")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getHolidayList() {
		LOGGER.info("in get Holiday list Controller");
		List<Holidaycalendar> List = holidayCalenderService.getHolidayList();
		JSONArray holidayList = getHolidayList(List);
		LOGGER.info("Holidays send to the UI  " + holidayList.toString());
		return Response.status(201).entity(holidayList.toString()).build();
	}

	/**
	 * Create a rest api method to insert details of the holidays in database
	 * 
	 * @param Calendar
	 *            cal : Holiday Calendar Object
	 * @returns Response
	 */
	@POST
	@Path("/addCalendarRow")
	@Consumes("application/json")
	public Response addCalendarRow(HolidayCalendar cal,@Context HttpServletRequest request) {
		LOGGER.info("In Addtion Holiday controller  " + cal.toString());
		HttpSession session = request.getSession(false);
		if(session==null){
			return Response.status(403).entity("Inactive session").build();
		}else{
			Date holidayDate = cal.getHolidayDate();
			String holiday = cal.getHoliday();
			Departmentdetails departmentDetails = masterAssociateService.getDepartmentByName(cal.getDepartment());
			Holidaycalendar holidaycalendar = new Holidaycalendar(departmentDetails, holidayDate, holiday);
			holidayCalenderService.insertHoliday(holidaycalendar);
			return Response.ok("result").status(201).build();
		}
		
	}

	/**
	 * Create a rest api method to update details of the holidays in database
	 * 
	 * @param Calendar
	 *            [] cal : Holiday Calendar Array
	 * @returns Response
	 */
	@PUT
	@Path("/editCalendarRow")
	@Consumes("application/json")
	public Response editCalendarRow(HolidayCalendar[] calRow,@Context HttpServletRequest request) {
		LOGGER.info("in edit calendar ");
		HttpSession session = request.getSession(false);
		if(session==null){
			return Response.status(403).entity("Inactive session").build();
		}else{
			for (HolidayCalendar cal : calRow) {
				holidayCalenderService.updateCalendarRow(cal);
			}
			return Response.ok("Updated").status(201).build();
		}
		
	}

	/**
	 * Create a rest api method to insert details of the holidays in database
	 * 
	 * @param Calendar
	 *            cal : Holiday Calendar Object
	 * @returns Response
	 */
	@DELETE
	@Path("/deleteCalendarRow")
	@Consumes("application/json")
	public Response deleteCalendarRow(HolidayCalendar[] calRow,@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session==null){
			return Response.status(403).entity("Inactive session").build();
		}else{
			for (HolidayCalendar cal : calRow) {
				Holidaycalendar holidaycalendar = holidayCalenderService.getHolidaycalendarById(cal.getId());
				LOGGER.info("Calender in delete" + cal.toString());
				holidayCalenderService.deleteCalendarRow(holidaycalendar);
			}
			return Response.ok("result").status(201).build();
		}
		
	}
	/**
	 * Create a rest api method to get details of the holidays dates from database
	 * 
	 * @returns Response
	 */
	@GET
	@Path("/holidayDates")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response getHolidayListDates() {
		
		LOGGER.info("in get Holiday list Controller");
		List<Holidaycalendar> List = holidayCalenderService.getHolidayList();
		JSONArray holidayList = getHolidayListDates(List);
		LOGGER.info("Holidays send to the UI  " + holidayList.toString());
		return Response.status(201).entity(holidayList.toString()).build();
	}

	/*
	 * Getting the holiday list to send to the UI
	 */

	private JSONArray getHolidayList(List<Holidaycalendar> list) {
		JSONArray holidayList = new JSONArray();

		for (Holidaycalendar holidaycalendar : list) {
			JSONObject holiday1 = new JSONObject();
			Date holidayDate = holidaycalendar.getHolidayDate();
			String holiday = holidaycalendar.getHoliday();
			SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
			String holidayDay = simpleDateformat.format(holidayDate);
			Departmentdetails departmentdetails = holidaycalendar.getDepartmentdetails();
			String department = departmentdetails.getDepartmentName();
			try {
				holiday1.put("id", holidaycalendar.getId());
				holiday1.put("holidayDate", holidayDate);
				holiday1.put("holiday", holiday);
				holiday1.put("department", department);
				holiday1.put("holidayDay", holidayDay);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			holidayList.put(holiday1);
		}
		return holidayList;
	}
	
	/*
	 * Getting the holiday list to send to the UI
	 */

	private JSONArray getHolidayListDates(List<Holidaycalendar> list) {
		JSONArray holidayList = new JSONArray();

		for (Holidaycalendar holidaycalendar : list) {
			Date holidayDate = holidaycalendar.getHolidayDate();
			holidayList.put(holidayDate);
		}
		return holidayList;
	}

}
