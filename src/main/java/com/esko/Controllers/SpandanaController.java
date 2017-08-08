package com.esko.Controllers;

import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.esko.Service.AttendanceFeedService;
import com.esko.Service.LeaveFeedService;
import com.esko.Service.MasterAssociateService;
import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.Plugin.Converter;
import com.esko.Utils.Plugin.LeaveConverter;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Component
@Path("/spandanaController")
public class SpandanaController {

	final static Logger LOGGER = Logger.getLogger(SpandanaController.class.getName());
	@Inject
	private AttendanceFeedService attendanceService;
	@Inject
	private LeaveFeedService leaveService;
	@Inject
	private MasterAssociateService masterAssociateService;

	/*
	 * Validating the uploaded leaveFeed CSV and generating json for the
	 * successfully parsed and unsuccessful data
	 */
	@POST
	@Path("/Leave")
	@Consumes(javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
	public Response processLeaveCSV(@FormDataParam("files") InputStream uploadedInputStream,
			@FormDataParam("files") FormDataContentDisposition fileDispositions, @Context HttpServletRequest request) {
		LOGGER.info("Processing Leave feed Controller");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		} else {
			JSONObject parsedCSVJSON = new JSONObject();
			String fileName = fileDispositions.getFileName();
			try {
				if (!(fileName.contains(".xlsx"))) {
					throw new UnsupportedFileException("File format is not correct!");
				}
				LOGGER.info("processing file" + fileName);

				LeaveConverter converter = LeaveConverter.getDefault();
				LOGGER.info("calling converter ");
				converter.convert(uploadedInputStream, fileName);
				LOGGER.info("successfully converted file ");
				parsedCSVJSON = leaveService.read();

			} catch (UnsupportedFileException e) {
				parsedCSVJSON.put("parsed", "Unsuccessful");
			} catch (Exception e) {
				parsedCSVJSON.put("parsed", "Unsuccessful");
			}
			ResponseBuilder response = Response.ok(parsedCSVJSON.toString());
			return response.build();
		}

	}

	/*
	 * Validating the uploaded attendanceFeed CSV and generating json for the
	 * successfully parsed and unsuccessfully parsed data
	 */
	@POST
	@Path("/Attendance")
	@Consumes(javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
	public Response processCSV(@FormDataParam("files") InputStream uploadedInputStream,
			@FormDataParam("files") FormDataContentDisposition fileDispositions, @Context HttpServletRequest request) {
		LOGGER.info("Processing Attendance feed Controller");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		}else{
			Converter converter = Converter.getDefault();
			JSONObject parsedCSVJSON = new JSONObject();
			try {
				converter.convert(uploadedInputStream);
				parsedCSVJSON = attendanceService.read();
			} catch (UnsupportedFileException e) {
				parsedCSVJSON.put("parsed", "Unsuccessful");
			}
			ResponseBuilder response = Response.ok(parsedCSVJSON.toString());
			return response.build();
		}
		
	}

	/*
	 * Validating the uploaded MasterFeed CSV and generating json for the
	 * successfully parsed and unsuccessfully parsed data
	 */
	@POST
	@Path("/Master")
	@Consumes(javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
	public Response processMasterCSV(@FormDataParam("files") InputStream uploadedInputStream,
			@FormDataParam("files") FormDataContentDisposition fileDispositions, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(403).entity("Inactive session").build();
		}else{
			LOGGER.info("Processing Master feed Controller");
			JSONObject parsedCSVJSON = masterAssociateService.read(uploadedInputStream);
			ResponseBuilder response = Response.ok(parsedCSVJSON.toString());
			return response.build();
		}
		
	}

}