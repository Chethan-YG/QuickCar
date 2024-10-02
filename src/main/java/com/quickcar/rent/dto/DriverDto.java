package com.quickcar.rent.dto;

import java.util.Date;

import com.quickcar.rent.enums.HireDriverStatus;
import lombok.Data;

@Data
public class DriverDto {
    private Long id;
    private Date fromDate;
    private Date toDate;
    private Long days;
    private HireDriverStatus hireStatus;
    private Long driverId;
    private Long userId;
}
