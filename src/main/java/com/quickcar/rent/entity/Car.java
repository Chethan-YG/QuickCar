package com.quickcar.rent.entity;

import lombok.Data;

import com.quickcar.rent.dto.CarDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String name;
    private String type;
    private String transmission;
    private String color;
    private int year;
    @Column(name = "fuel_type")  
    private String fuelType;
    private int price;
    private String imageUrl;
    private String invoiceUrl;
    private int odometer;
    
    public CarDto getCarDTO() {
        return new CarDto(id, brand,name, type, transmission, color,year,fuelType, price,imageUrl,invoiceUrl,odometer);
    }
}

