package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.dto.RegisterRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefeshTokenRequest;
import com.example.demo.dto.TokenPair;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	@Autowired
	private AuthService authService;

//	@PostMapping("/register")
//	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request){
//		authService.registerUser(request);
//		return ResponseEntity.ok("User Regestered Successfully");
//	}
	
	@PostMapping("/register")
	public ResponseEntity<TokenPair> registerAndLogin(
	    @Valid @RequestBody RegisterRequest req
	) {
	    authService.registerUser(req);              // create user
	    TokenPair tokens = authService.login(       // then log them in
	        new LoginRequest(req.getUsername(), req.getPassword())
	    );
//	    return ResponseEntity
//	      .status(HttpStatus.CREATED)
//	      .body(tokens);
	    
	    ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.getAccessToken())
		        .httpOnly(true)
		        .secure(true)             // only over HTTPS
		        .path("/")                // sent on every request
		        .maxAge(15 * 60)          // 15 minutes
		        .sameSite("Strict")       // or "Lax" if you need cross-site GETs
		        .build();

		    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.getRefreshToken())
		        .httpOnly(true)
		        .secure(true)
		        .path("/api/auth/refresh-token") // only sent when calling /refresh-token
		        .maxAge(7 * 24 * 60 * 60)        // e.g. 7 days
		        .sameSite("Strict")
		        .build();

		    // 3) Return 200 with Set-Cookie headers
		    return ResponseEntity.ok()
		        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
		        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
		        .build();
	}

	
//	@PostMapping(
//			  path     = "/login",
//			  consumes = MediaType.APPLICATION_JSON_VALUE,
//			  produces = MediaType.APPLICATION_JSON_VALUE
//			)
//	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
//		TokenPair tokenPair = authService.login(loginRequest);
//		return ResponseEntity.ok(tokenPair); 
//		
////		authenticate the user
////		return access token and refresh token
//	}
	
	@PostMapping(
			  path     = "/login",
			  consumes = MediaType.APPLICATION_JSON_VALUE
			)
			public ResponseEntity<Void> login(
			    @Valid @RequestBody LoginRequest loginRequest
			) {
			    // 1) Authenticate & build tokens
			    TokenPair tokens = authService.login(loginRequest);

			    // 2) Create HttpOnly cookies
			    ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.getAccessToken())
			        .httpOnly(true)
			        .secure(true)             // only over HTTPS
			        .path("/")                // sent on every request
			        .maxAge(15 * 60)          // 15 minutes
			        .sameSite("Strict")       // or "Lax" if you need cross-site GETs
			        .build();

			    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.getRefreshToken())
			        .httpOnly(true)
			        .secure(true)
			        .path("/api/auth/refresh-token") // only sent when calling /refresh-token
			        .maxAge(7 * 24 * 60 * 60)        // e.g. 7 days
			        .sameSite("Strict")
			        .build();

			    // 3) Return 200 with Set-Cookie headers
			    return ResponseEntity.ok()
			        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
			        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			        .build();
			}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody RefeshTokenRequest request){
		TokenPair tokenPair = authService.refreshToken(request);
		return ResponseEntity.ok(tokenPair);
	}
}
