package com.quickcar.rent.dto;

import com.quickcar.rent.enums.UserRole;

import lombok.Data;

@Data
public class Userdto {
	private Long id;

	private String name;

	private String email;

	private UserRole userRole;

}
