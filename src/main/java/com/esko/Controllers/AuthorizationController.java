package com.esko.Controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;



@Component
@Path("/authorizationController")
public class AuthorizationController {
	
	final static Logger LOGGER = Logger.getLogger(AuthorizationController.class.getName());
	
	@POST
	@Path("{role}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isAuthorized(@PathParam("role") String role, @Context HttpServletRequest request)
	{
		LOGGER.info("-------Authorizing URL with Role------- ");
		JSONObject jSON = new JSONObject();
		System.out.println(role);
		HttpSession session = request.getSession(false);
		if(session !=null)
		{
			LOGGER.info("-------Session exists for the current user------- ");
			if(session.getAttribute("role").equals(role))
			{
				jSON.put("authorized", true);
				
			}
			else
			{
				jSON.put("authorized", false);
				jSON.put("value", "invalid");
			}
				
		}
		/* when session is not there redirect user to login*/
		else
		{
			LOGGER.info("-------Session doesn't exists ------- ");
			jSON.put("authorized", "failure");
		}

		ResponseBuilder response = Response.ok(jSON.toString());
		return response.build();
	}
	
	@GET
	@Path("/checkSession")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isLoggedIn(@Context HttpServletRequest request)
	{
		LOGGER.info("-------in validate session------- ");
		JSONObject jSON = new JSONObject();
		HttpSession session = request.getSession(false);
		if(session !=null)
		{
			LOGGER.info("-------Session exists for the current user------- ");
			if(session.getAttribute("role") !=null)
			{
				String role=(String) session.getAttribute("role");
				jSON.put("loggedin", true);
				jSON.put("role", role);
				
			}
			else
			{
				jSON.put("loggedin", "invalid");
				
			}
				
		}
		else
		{
			LOGGER.info("-------Session doesn't exists so user has to redirect to login------- ");
			jSON.put("loggedin", false);
		}
		ResponseBuilder response = Response.ok(jSON.toString());
		return response.build();
		
	}

}
