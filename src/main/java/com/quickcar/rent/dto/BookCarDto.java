package com.quickcar.rent.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.quickcar.rent.enums.BookCarStatus;

import lombok.Data;

@Data
public class BookCarDto {
    private Long id;
    private Date fromDate;
    private Date toDate;
    private Long days;
    private Long price;
    private BookCarStatus bookCarStatus;
    private Long carId;
    private Long userId;
    private String licenseImage;
}
