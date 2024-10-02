package com.quickcar.rent.service.driver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.entity.BookCar;
import com.quickcar.rent.entity.Driver;
import com.quickcar.rent.entity.HireDriver;
import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.HireDriverStatus;
import com.quickcar.rent.exception.ResourceNotFoundException;
import com.quickcar.rent.repository.DriverRepository;
import com.quickcar.rent.repository.HireDriverRepository;
import com.quickcar.rent.repository.UserRepository;
import com.quickcar.rent.service.customer.CustomerServiceImpl;
import com.quickcar.rent.service.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService{
	
	private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
	private final DriverRepository driverRepository;
	private final Cloudinary cloudinary;
	private final UserRepository userRepository;
	private final HireDriverRepository hireDriverRepository;
	private final MailService mailService;

	@Override
	public List<Driver> getAllDrivers() {
		
		return driverRepository.findAll();
	}

	@Override
	public Driver getDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

	@Override
    public Driver saveDriver(Driver driver, MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
	        try {
	            Map<String, Object> uploadImageResult = cloudinary.uploader().upload(file.getBytes(),
	                    ObjectUtils.emptyMap());
	            driver.setProfilePicUrl(uploadImageResult.get("url").toString());
	        } catch (IOException e) {
	            throw new IOException("Error uploading file to Cloudinary", e);
	        }
	    }

	    return driverRepository.save(driver);
    }

	@Override
	public Driver updateDriver(Long id, Driver driverDetails, MultipartFile file) throws IOException{
	    Driver driver = getDriverById(id);
	    if (driver == null) {
	        throw new ResourceNotFoundException("Driver with ID " + id + " not found");
	    }

	    if (driverDetails != null) {
	        driver.setName(driverDetails.getName());
	        driver.setExpertise(driverDetails.getExpertise());
	        driver.setLanguages(driverDetails.getLanguages());
	        driver.setLicence(driverDetails.getLicence());
	        driver.setPhNo(driverDetails.getPhNo());
	        driver.setYoe(driverDetails.getYoe());
	    }

	    if (file != null && !file.isEmpty()) {
	        try {
	            Map<String, Object> uploadImageResult = cloudinary.uploader().upload(file.getBytes(),
	                    ObjectUtils.emptyMap());
	            driver.setProfilePicUrl(uploadImageResult.get("url").toString());
	        } catch (IOException e) {
	            throw new IOException("Error uploading file to Cloudinary", e);
	        }
	    }

	    return driverRepository.save(driver);
	}


	@Override
    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

	
	@Override
	@Transactional
	public boolean hireDriver(DriverDto driverDto) {
	    Optional<Driver> optionalDriver = driverRepository.findById(driverDto.getDriverId());
	    Optional<User> optionalUser = userRepository.findById(driverDto.getUserId());

	    if (optionalDriver.isPresent() && optionalUser.isPresent()) {
	        //log.info("Received DriverDto: {}", driverDto);
	        Driver driver = optionalDriver.get();
	        User user = optionalUser.get();
	        
	        HireDriver hireDriver = new HireDriver();
	        hireDriver.setUser(user);
	        hireDriver.setDriver(driver);
	        hireDriver.setHireDriverStatus(HireDriverStatus.PENDING);
	        hireDriver.setFromDate(driverDto.getFromDate());
	        hireDriver.setToDate(driverDto.getToDate());
	        
	        long diffMilliseconds = driverDto.getToDate().getTime() - driverDto.getFromDate().getTime();
	        long days = TimeUnit.MILLISECONDS.toDays(diffMilliseconds);
	        hireDriver.setDays(days);	        
	        hireDriverRepository.save(hireDriver);
	        mailService.sendDriverStatusEmail(user.getEmail(), hireDriver.getDriver().getName(), "Pending");
	        return true;
	    }
	    return false;
	}

	@Override
	public List<HireDriver> getAllHiredDrivers() {
		return hireDriverRepository.findAll();
	}
	
	@Override
	public HireDriver updateBookingStatus(Long id, HireDriverStatus hireDriverStatus) {
		HireDriver driver=hireDriverRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("Hiring not found with id: "+ id));
		driver.setHireDriverStatus(hireDriverStatus);
		mailService.sendDriverStatusEmail(driver.getUser().getEmail(), driver.getDriver().getName(), hireDriverStatus.toString().toLowerCase());
		return hireDriverRepository.save(driver);
	}

	@Override
	public List<DriverDto> getHiredDates(Long driverId) {
		List<HireDriver> hirings = hireDriverRepository.findByDriverId(driverId);
        return hirings.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private DriverDto convertToDTO(HireDriver hireDriver) {
    	DriverDto dto=new DriverDto();
    	dto.setId(hireDriver.getId());
		dto.setFromDate(hireDriver.getFromDate());
		dto.setToDate(hireDriver.getToDate());
		dto.setDays(hireDriver.getDays());
		dto.setHireStatus(hireDriver.getHireDriverStatus());
		dto.setDriverId(hireDriver.getDriver().getId());
		dto.setUserId(hireDriver.getUser().getId());
		return dto;
    }
}
