package com.example.demo.dto;

import lombok.Data;

@Data
public class TokenPair {
	
	private String accessToken;
	private String refreshToken;
	
	public TokenPair() {
		
	}
	
	public TokenPair(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		System.out.println("❌ From TOken Pair: ");
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
	
	
	
	
}
