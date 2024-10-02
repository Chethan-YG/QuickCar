package com.quickcar.rent.dto;

import lombok.Data;

@Data
public class SignUpRequest {
	
	private String name;
	private String email;
	private String password;
	public String phNo;
	

}
