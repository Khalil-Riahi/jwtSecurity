package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repository.UserRepository;
import com.example.demo.controller.dto.RegisterRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefeshTokenRequest;
import com.example.demo.dto.TokenPair;
import com.example.demo.model.User;

import jakarta.validation.Valid;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    

    @Transactional
    public void registerUser(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setFullName(req.getFullName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        
        

        userRepo.save(user);
    }
    
    public TokenPair login(LoginRequest loginRequest) {
//    	Authenticate the user 
    	try {
    		Authentication auhtentication = authenticationManager.authenticate(
        			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        	);
//        	set authentication in Security Context
        	SecurityContextHolder.getContext().setAuthentication(auhtentication);
        	
        	System.out.println("❌ hello hello: ");
//        	Generate Token Pair
        	return jwtService.generateTokenPair(auhtentication);
    	}catch(Exception e) {
    		System.out.println("❌ Login failed: " + e.getMessage());
            throw e; // rethrow or handle appropriately
    	}
    	
    }

	public TokenPair refreshToken(@Valid RefeshTokenRequest request) {
		// check if it is valid refresh token
		
		String refreshToken = request.getRefreshToken();
		
		if(!jwtService.isRefreshToken(request.getRefreshToken())) {
			throw new IllegalArgumentException("Invalid Refresh Token");
		}
		
		String 	user = jwtService.extractUsernameFromToken(refreshToken);
		UserDetails userDetails = userDetailsService.loadUserByUsername(user);
		
		if(userDetails == null) {
			throw new IllegalArgumentException("User not found");
		}
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails , null , userDetails.getAuthorities()
		);
		
		String accessToken = jwtService.generateAccessToken(authentication);
		return new TokenPair(accessToken , refreshToken);
	}
}
