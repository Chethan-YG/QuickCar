package com.quickcar.rent.service.customer;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.entity.User;

public interface CustomerService {
	boolean bookCar(BookCarDto bookCarDto, MultipartFile licenceImage);
	public List<BookCarDto> getBookingsByUserId(Long userId);
	public Optional<User> getUserById(Long Id);
	public List<DriverDto> getHiringsByUserId(Long userId);	
	public Optional<User> findByEmail(String email);
}
