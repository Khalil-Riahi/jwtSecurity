package com.example.demo.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		Inetercept the request
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String username;
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		jwt = getJwtFormRequest(request);
		
		if(!jwtService.isValidToken(jwt)) {
			filterChain.doFilter(request , response);
			return;
		}
		username = jwtService.extractUsernameFromToken(jwt);
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if(jwtService.validateTokenForUser(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken .setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
						
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
			
			filterChain.doFilter(request, response);
		}
		
		
//		Do the validation
//		Authenticate the user
		// TODO Auto-generated method stub
		
		
		
	}



	private String getJwtFormRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		final String authHeader = request.getHeader("Authorization");
		return authHeader.substring(7);

	}
 
}
