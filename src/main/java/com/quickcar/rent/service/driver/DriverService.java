package com.quickcar.rent.service.driver;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.entity.Driver;
import com.quickcar.rent.entity.HireDriver;
import com.quickcar.rent.enums.HireDriverStatus;

public interface DriverService {
	public List<Driver> getAllDrivers();
	public boolean hireDriver(DriverDto driverDto);
	public void deleteDriver(Long id);
	public Driver updateDriver(Long id, Driver driverDetails, MultipartFile file) throws IOException, java.io.IOException;
	public Driver saveDriver(Driver driver, MultipartFile file) throws IOException;
	public Driver getDriverById(Long id);
	public List<HireDriver> getAllHiredDrivers();
	public HireDriver updateBookingStatus(Long id, HireDriverStatus hireDriverStatus);
	public List<DriverDto> getHiredDates(Long driverId);	
}
