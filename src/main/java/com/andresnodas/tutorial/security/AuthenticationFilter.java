package com.andresnodas.tutorial.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.andresnodas.tutorial.SpringAppContext;
import com.andresnodas.tutorial.dto.UserDto;
import com.andresnodas.tutorial.model.request.UserLoginRequestModel;
import com.andresnodas.tutorial.model.response.OperationStatusModel;
import com.andresnodas.tutorial.model.response.RequestOperationName;
import com.andresnodas.tutorial.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		UserLoginRequestModel userLoginRequestModel = null;
		
		try {
			userLoginRequestModel = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				userLoginRequestModel.getEmail(), 
				userLoginRequestModel.getPassword(), 
				new ArrayList<>()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		
		String username = ((User) authResult.getPrincipal()).getUsername();
		
		String token = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
				.compact();
		
		UserService userService = (UserService) SpringAppContext.getBean("userServiceImpl");
		UserDto userDto = userService.getUser(username);
		
		PrintWriter out = response.getWriter();
		
		response.setHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
		response.setHeader("UserId", userDto.getUserId());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.print("{ token : " + token + "}");
		out.flush();
		
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.LOGIN.name());
		operationStatusModel.setOperationResult(failed.getMessage());
		
		out.print(new ObjectMapper().writeValueAsString(operationStatusModel));
		out.flush();
	}
	
}
