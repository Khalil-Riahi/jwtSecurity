package com.example.demo.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.dto.TokenPair;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	
	@Value("${app.jwt.secret}")
	private String jwtSecret;
	
	@Value("${app.jwt.expiration}")
	private long jwtExpirationMs;
	
	@Value("${app.jwt.refresh-expiration}")
	private long refreshExpirationMs;
	
//	private static final String TOKEN_PREFIX = "Bearer ";
	
	//	Generate Access Token
	public String generateAccessToken(Authentication authentication) {
		return generateToken(authentication , jwtExpirationMs , new HashMap<>());
	}
	
	public TokenPair generateTokenPair(Authentication authentication) {
		String accessToken = generateAccessToken(authentication);
		String refreshToken = generateRefreshToken(authentication);
		System.out.println("❌ kif: ");
		return new TokenPair(accessToken , refreshToken);
	}

	
	
	public String generateRefreshToken(Authentication authentication) {
		
		
		Map<String , String> claims = new HashMap<>();
		
		claims.put("tokenType", "refresh");
		
		return generateToken(authentication, refreshExpirationMs , claims);
	}
	
	
	private String generateToken(Authentication authentication , long expirationMs , Map<String , String> claims) {
		UserDetails userPricipal = (UserDetails) authentication.getPrincipal();
		
		Date now = new Date();
		
		Date expiryDate = new Date(now.getTime() + expirationMs);
		
		return Jwts.builder().header().add("typ" , "JWT").and().subject(userPricipal.getUsername()).claims(claims).issuedAt(now).expiration(expiryDate).signWith(getSignInKey()).compact();
	}
	
	public boolean validateTokenForUser(String token , UserDetails userDetails) {
		final String username = extractUsernameFromToken(token);
		
		return username != null && username.equals(userDetails.getUsername());
	}
	
	public boolean isValidToken(String token) {
		return extracteAllClaims(token) != null;
	}
	
	public boolean isRefreshToken(String token) {
		Claims claims = extracteAllClaims(token);
		
		if(claims == null) {
			return false; 
		}
		return "refresh".equals(claims.get("tokenType"));
	}

	private Claims extracteAllClaims(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
		} catch (JwtException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return claims;
	}
	
	public String extractUsernameFromToken(String token) {
		Claims claims = extracteAllClaims(token);
		
		if(claims != null) {
			return claims.getSubject();
		}
		
		return null;
	}
	
	
	

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		
		return Keys.hmacShaKeyFor(keyBytes);
	}



	

}
