package com.esko.Controllers;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.esko.Model.Employeedetails;
import com.esko.Service.EmployeeRoleService;
import com.esko.Service.MasterAssociateService;
import com.esko.Utils.Pdf.PropertiesFile;

import sun.misc.BASE64Decoder;

@Component
@Path("/loginController")
public class LoginController {
	final static Logger LOGGER = Logger.getLogger(LoginController.class.getName());
	@Inject
	private MasterAssociateService masterAssociateService;
	@Inject
	private EmployeeRoleService employeeRoleService;
	private static final String SPLIT_BY_MULTIPLY = "\\*";

	/*
	 * 
	 * creates the session once user logged in
	 * 
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@HeaderParam("authorization") String authString, @Context HttpServletRequest request) {
		LOGGER.info("-------login invoked and creating session for user------- ");
		
		JSONObject jSON = new JSONObject();
		String uid = "";
		String pwd = "";
		String decodedAuth = "";
		String[] authParts = authString.split("\\s+");
		String authInfo = authParts[1];
		byte[] bytes = null;

		try {
			bytes = new BASE64Decoder().decodeBuffer(authInfo);
			decodedAuth = new String(bytes);
			String[] credentials = decodedAuth.split(":");
			uid = credentials[0];
			pwd = credentials[1];
			Employeedetails employeedetails = masterAssociateService.checkEmployee(uid);
			if (employeedetails != null) {
				String sessionTimeOut = PropertiesFile.readProperties("spandana.properties").getProperty("SessionTimeOut");
				String values[] = sessionTimeOut.split(SPLIT_BY_MULTIPLY);
				int sessionTime = 1;
				for(int i=0;i<values.length;i++){
					int x = Integer.parseInt(values[i]);
					sessionTime = sessionTime*x;
				}
				String employeeId = employeedetails.getEmployeeId();
				String role = employeeRoleService.getEmployeeRole(employeedetails);
				HttpSession session = request.getSession();
				session.setAttribute("employeeId", employeeId);
				session.setAttribute("role", role);
				session.setMaxInactiveInterval(sessionTime);
				jSON.put("result", "success");
				jSON.put("role", role);
				jSON.put("employeeId", employeeId);
			} else {
				jSON.put("result", "failure");
			}
		}  
		catch (IOException e) {
			jSON.put("result", "failure");
		} catch (Exception e) {
			e.printStackTrace();
			jSON.put("result", "failure");
		}
		ResponseBuilder response = Response.ok(jSON.toString());
		return response.build();
	}

	/*
	 * Creates a rest api to add employee roles
	 */
	@GET
	@Path("/Employee")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response addEmployeeRoles() {
		LOGGER.info("in get department details controller");
		employeeRoleService.addEmployeeRoles();
		return Response.status(201).entity("Added").build();
	}

	/*
	 * Creates a rest api to add employee roles
	 */
	@GET
	@Path("/Logout")
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
	public Response logout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session==null){
			return Response.status(403).entity("session inactive").build();
		}else{
			session.invalidate();
			return Response.status(201).entity("logout successful").build();
		}
	}
}
