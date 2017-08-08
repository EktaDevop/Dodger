package com.esko.Security;

import com.google.gson.Gson;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/*
 * invoked when user access Resources which are not allowed
 * */
public class AccessDeniedHandler implements
        org.springframework.security.web.access.AccessDeniedHandler {
	final static Logger LOGGER = Logger.getLogger(AccessDeniedHandler.class.getName());

    @Override
    public void handle(HttpServletRequest request
            , HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
    	LOGGER.info("In Access Denied Handler");
        final Map<String,String> apiServer=new HashMap<String, String>();
        apiServer.put("MESSAGE","ACCESS DENIED");
        apiServer.put("STATUS", "" + HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new Gson().toJson(apiServer));
        response.addHeader("MESSAGE","ACESS DENIED");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
