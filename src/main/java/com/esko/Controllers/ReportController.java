package com.esko.Controllers;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.csvreader.CsvWriter;
import com.esko.FrontEndObjects.AbsentDetails;
import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.FrontEndObjects.LeaveId;
import com.esko.FrontEndObjects.NotinofficeId;
import com.esko.Model.Attendancefeed;
import com.esko.Model.AttendancefeedId;
import com.esko.Model.Holidaycalendar;
import com.esko.Model.Leavefeed;
import com.esko.Model.Notinoffice;
import com.esko.Other.DateConverter;
import com.esko.Other.ResourcePath;
import com.esko.Service.AttendanceFeedService;
import com.esko.Service.HolidayCalenderService;
import com.esko.Service.LeaveFeedService;
import com.esko.Service.MasterAssociateService;
import com.esko.Service.NotInOfficeService;
import com.esko.Utils.Pdf.FirstPdf;
import com.esko.Utils.Pdf.PropertiesFile;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Component
@Path("/reportController")
public class ReportController {
	final static Logger LOGGER = Logger.getLogger(ReportController.class.getName());
	@Inject
	private AttendanceFeedService attendanceService;
	@Inject
	private LeaveFeedService leaveService;
	@Inject
	private MasterAssociateService masterAssociateService;
	@Inject
	private HolidayCalenderService holidayCalService;
	@Inject
	private NotInOfficeService notInOfficeService;

	/*
	 * generating the report for the present working employees
	 */
	@POST
	@Path("/employeeRecords")
	@Consumes("application/json")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response getEmployeeRecord(String[] dateStringArray, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			List<AbsentDetails> empRecList = new ArrayList<AbsentDetails>();
			Date startDate = DateConverter.getDateInputString("yyyy-MM-dd", dateStringArray[0]);
			Date endDate = DateConverter.getDateInputString("yyyy-MM-dd", dateStringArray[1]);
			int dayDifference = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000) + 1);
			List<Date> dateRangeList = DateConverter.getDateRange(startDate, dayDifference);

			List<Leavefeed> leaveList = leaveService.listLeaveFeed(startDate, endDate);
			Multimap<LeaveId, Leavefeed> leaveMap = ArrayListMultimap.create();
			for (Leavefeed leavefeed : leaveList) {
				LeaveId leaveId = new LeaveId(leavefeed.getId().getEmployeeIdLf(),
						DateConverter.getDateInputDate("yyyy-MM-dd", leavefeed.getId().getLeaveDate()));
				leaveMap.put(leaveId, leavefeed);
			}

			List<Attendancefeed> attendanceList = attendanceService.listAttendanceFeed(startDate, endDate);
			Map<AttendancefeedId, Attendancefeed> attendanceMap = new HashMap<AttendancefeedId, Attendancefeed>();
			for (Attendancefeed attendancefeed : attendanceList) {
				attendanceMap.put(attendancefeed.getId(), attendancefeed);
			}

			List<Holidaycalendar> holidayCal = holidayCalService.getHolidayList();
			Map<Date, Holidaycalendar> holidayCalMap = new HashMap<Date, Holidaycalendar>();
			for (Holidaycalendar holidaycalendar : holidayCal) {
				holidayCalMap.put(holidaycalendar.getHolidayDate(), holidaycalendar);
			}

			List<Notinoffice> notInOfficeList = notInOfficeService.getNotInOfficeList(startDate, endDate);
			Map<NotinofficeId, Notinoffice> notInOfficeMap = new HashMap<NotinofficeId, Notinoffice>();
			for (Notinoffice notInOffice : notInOfficeList) {
				Date start = DateConverter.getDateInputDate("yyyy-MM-dd", notInOffice.getDateOfRequestStart());
				Date end = DateConverter.getDateInputDate("yyyy-MM-dd", notInOffice.getDateOfRequestEnd());
				if (start.equals(end)) {
					NotinofficeId notinofficeId = new NotinofficeId(notInOffice.getDateOfRequestStart(),
							notInOffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId());
					notInOfficeMap.put(notinofficeId, notInOffice);
				} else {
					int dayDiff = (int) ((notInOffice.getDateOfRequestEnd().getTime()
							- notInOffice.getDateOfRequestStart().getTime()) / (24 * 60 * 60 * 1000) + 1);
					List<Date> dateRange = DateConverter.getDateRange(notInOffice.getDateOfRequestStart(), dayDiff);
					for (Date dateitr : dateRange) {
						NotinofficeId notinofficeId = new NotinofficeId(dateitr,
								notInOffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId());
						notInOfficeMap.put(notinofficeId, notInOffice);
					}
				}
			}

			List<EmployeeDetails> employeeDetailsList = masterAssociateService.getEmployeeDetails();
			for (EmployeeDetails e : employeeDetailsList) {
				for (Date datei : dateRangeList) {
					Date dateitr = DateConverter.getDateInputDate("yyyy-MM-dd", datei);
					Attendancefeed attendancefeed = attendanceMap.get(new AttendancefeedId(e.getEmployeeId(), dateitr));
					if (attendancefeed == null) {
						Calendar cal = getDate(dateitr);
						if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
								&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
							if ((holidayCalMap.get(dateitr) == null) || ((holidayCalMap.get(dateitr)
									.getDepartmentdetails().getDepartmentName().compareTo(e.getDepartmentId()) != 0)
									&& (holidayCalMap.get(dateitr).getDepartmentdetails().getDepartmentName()
											.compareToIgnoreCase("All") != 0))) {
								if (notInOfficeMap.get(new NotinofficeId(dateitr, e.getEmployeeId())) == null) {
									List<Leavefeed> list = (List<Leavefeed>) leaveMap
											.get(new LeaveId(e.getEmployeeId(), dateitr));
									if (list.size() == 0) {
										empRecList.add(new AbsentDetails(e.getEmployeeId(), e.getManagerId(),
												e.getDepartmentId(), dateitr, "Full", "0:00", "0:00", "Absent"));
									} else {
										if (list.size() == 1) {
											if (list.get(0).getId().getDuration().compareTo("Full") != 0) {
												String day = list.get(0).getId().getDuration();
												if (day.equalsIgnoreCase("First_Half")) {
													day = "Second_Half";
												} else {
													day = "First_Half";
												}
												empRecList.add(new AbsentDetails(e.getEmployeeId(), e.getManagerId(),
														e.getDepartmentId(), dateitr, day, "0:00", "0:00",
														"not applied for leave"));
											}
										}
									}
								}

							}
						}
					} else if ((attendancefeed.getDayDuration().compareToIgnoreCase("Full") != 0)
							&& (attendancefeed.getStatus().compareToIgnoreCase("Present") == 0)) {
						Calendar cal = getDate(dateitr);
						if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
								&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
							String day = attendancefeed.getDayDuration();
							List<Leavefeed> list = (List<Leavefeed>) leaveMap
									.get(new LeaveId(e.getEmployeeId(), dateitr));
							if (day.equalsIgnoreCase("First_Half")) {
								if (list.size() == 0) {
									day = "Second_Half";
									String checkIn = getTime(attendancefeed.getCheckIn());
									String checkOut = getTime(attendancefeed.getCheckOut());
									empRecList.add(new AbsentDetails(e.getEmployeeId(), e.getManagerId(),
											e.getDepartmentId(), dateitr, day, checkIn, checkOut,
											"Worked less than 8 hrs"));
								} else {
									if (list.size() == 1) {
										if (list.get(0).getId().getDuration().compareTo("Full") != 0) {
											day = list.get(0).getId().getDuration();
											if (day.equalsIgnoreCase("First_Half")) {
												// send notification
											}
										}
									}
								}
							} else {
								if (list.size() == 0) {
									day = "First_Half";
									String checkIn = getTime(attendancefeed.getCheckIn());
									String checkOut = getTime(attendancefeed.getCheckOut());
									empRecList.add(new AbsentDetails(e.getEmployeeId(), e.getManagerId(),
											e.getDepartmentId(), dateitr, day, checkIn, checkOut,
											"Worked less than 8 hrs and not applied leave for First-Half"));
								} else {
									if (list.size() == 1) {
										if (list.get(0).getId().getDuration().compareTo("Full") != 0) {
											day = list.get(0).getId().getDuration();
											if (day.equalsIgnoreCase("Second_Half")) {
												// send notification
											}
										}
									}
								}
							}
						}
					} else if ((attendancefeed.getStatus().compareToIgnoreCase("Present") != 0)) {
						String checkIn = getTime(attendancefeed.getCheckIn());
						String checkOut = getTime(attendancefeed.getCheckOut());
						String remarks = "";
						if (checkOut.equalsIgnoreCase("24:00:00")) {
							checkOut = "0:00";
							remarks = "Forgot to CheckOut";
						} else {
							remarks = "Worked less than 4 hrs";
						}
						empRecList.add(new AbsentDetails(e.getEmployeeId(), e.getManagerId(), e.getDepartmentId(),
								dateitr, "Full", checkIn, checkOut, remarks));
					}
				}
			}
			LOGGER.info(empRecList.toString());
			return Response.status(201).entity(empRecList).build();
		}

	}

	private String getTime(Date checkInTemp) {
		DateFormat df = new SimpleDateFormat("kk:mm:ss");
		String checkIn = df.format(checkInTemp);
		return checkIn;
	}

	/*
	 * Generating report in PDF / CSV format
	 */
	@POST
	@Path("/generateReport/{fileType}")
	@Consumes("application/json")
	public Response generatePDF(AbsentDetails[] empRecArray, @PathParam("fileType") String fileType,
			@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			List<AbsentDetails> empRecord = new ArrayList<AbsentDetails>();
			for (AbsentDetails empItr : empRecArray) {
				empRecord.add(empItr);
			}
			if (fileType.equalsIgnoreCase("pdf")) {
				FirstPdf fp = new FirstPdf();
				fp.generatePDF(PropertiesFile.readProperties("spandana.properties").getProperty("PDFName"), empRecord);
			} else {
				String[] header = null;
				ResourcePath resource = new ResourcePath();
				String resourcePath = "";
				try {
					resourcePath = resource.getResourcePath();
				} catch (Exception e1) {
				}
				header = PropertiesFile.readProperties("spandana.properties").getProperty("ReportHeaders").split(",");
				String outputFile = resourcePath
						+ PropertiesFile.readProperties("spandana.properties").getProperty("CSVName");
				File file = new File(outputFile);
				if (file.delete()) {
					LOGGER.info(file.getName() + " is deleted!");
				} else {
					LOGGER.info("Delete operation is failed.");
				}
				file = new File(outputFile);
				try {
					CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ',');
					for (String str : header) {
						csvOutput.write(str);
					}
					csvOutput.endRecord();
					for (AbsentDetails product : empRecord) {
						csvOutput.write(product.getEmployeeID());
						csvOutput.write(product.getManagerID());
						csvOutput.write(product.getDepartment());
						csvOutput.write(product.getDay());
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						String date = df.format(product.getAbsentRecordDate());
						csvOutput.write(date);
						csvOutput.write(product.getCheckIn());
						csvOutput.write(product.getCheckOut());
						csvOutput.write(product.getRemarks());
						csvOutput.endRecord();
					}
					csvOutput.close();
				} catch (java.io.IOException e) {
				}
			}
			return Response.ok("Success").status(200).build();
		}

	}

	/**
	 * Create a rest api method to get pdf
	 *
	 * @returns PDF Response
	 */
	@GET
	@Path("/getPDF")
	@Produces("application/pdf")
	public Response getPDF(@Context HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			File file = null;
			ResourcePath resource = new ResourcePath();
			String resourcePath = "";
			try {
				resourcePath = resource.getResourcePath();
			} catch (Exception e) {
			}
			file = new File(resourcePath + PropertiesFile.readProperties("spandana.properties").getProperty("PDFName"));

			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition", "attachment; filename="
					+ PropertiesFile.readProperties("spandana.properties").getProperty("PDFName"));

			return response.build();
		}

	}

	/**
	 * Create a rest api method to get csv
	 *
	 * @returns CSV Response
	 */
	@GET
	@Path("/getCSV")
	@Produces("text/csv")
	public Response getCSV(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			File file = null;
			ResourcePath resource = new ResourcePath();
			String resourcePath = "";
			try {
				resourcePath = resource.getResourcePath();
			} catch (Exception e) {
			}
			file = new File(resourcePath + PropertiesFile.readProperties("spandana.properties").getProperty("CSVName"));
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition", "attachment; filename="
					+ PropertiesFile.readProperties("spandana.properties").getProperty("CSVName"));
			return response.build();
		}

	}

	/**
	 * Create a method to get java.util.Calendar object from Date
	 *
	 * @param java
	 *            .util.Date inputDate:Date object which is to be converted
	 * @returns java.util.Calendar cal : java.util.Calendar Object
	 */
	public Calendar getDate(Date inputDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		return cal;
	}

}
