package com.quickcar.rent.dto;

import com.quickcar.rent.enums.UserRole;

import lombok.Data;

@Data
public class AuthenticationResponse {
	
	private String jwt;
	
	private UserRole  userRole;
	
	private Long userId;
	
	private String username;

}
