package com.quickcar.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
	private Long id;
	private String brand;
	private String name;
	private String type;
	private String transmission;
	private String color;
	private int year;
	private String fuelType;
	private int price;
	private String imageUrl;
	private String invoiceUrl;
	private int odometer;
}
