package com.quickcar.rent.dto;

import lombok.Data;

@Data
public class SearchCarDTO {
    private String brand;
    private String type;
    private String transmission;
    private String color;
}