package com.quickcar.rent.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.dto.CarDto;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.dto.SearchCarDTO;
import com.quickcar.rent.entity.BookCar;
import com.quickcar.rent.entity.Car;
import com.quickcar.rent.entity.Driver;
import com.quickcar.rent.entity.HireDriver;
import com.quickcar.rent.entity.MobileNumberRequest;
import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.BookCarStatus;
import com.quickcar.rent.enums.HireDriverStatus;
import com.quickcar.rent.repository.UserRepository;
import com.quickcar.rent.service.booking.BookingService;
import com.quickcar.rent.service.car.CarService;
import com.quickcar.rent.service.customer.CustomerService;
import com.quickcar.rent.service.driver.DriverService;
import com.razorpay.*;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;



@RestController
@RequestMapping("/api/customer")
@CrossOrigin
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;
	private final CarService carService;
	private final BookingService bookingService;
	private final UserRepository userRepository;
	private final DriverService driverService;
	@Value("${razorpay.client.key}")
	private String key;
	@Value("${razorpay.client.secret}")
	private String secret;

	@GetMapping("/bookings/{userId}")
	public ResponseEntity<List<BookCarDto>> getBookingsByUserId(@PathVariable Long userId) {
		List<BookCarDto> bookings = customerService.getBookingsByUserId(userId);
		return ResponseEntity.ok(bookings);
	}

	@GetMapping("/user/{id}")
	public Optional<User> getUserById(@PathVariable Long id) {
		return customerService.getUserById(id);
	}

	@PostMapping("/car/book")
    public ResponseEntity<?> bookCar(
            @RequestPart("car") String carJson,
            @RequestPart("licenseImage") MultipartFile licenseImage) throws IOException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        BookCarDto bookingRequest = objectMapper.readValue(carJson, BookCarDto.class);
        customerService.bookCar(bookingRequest, licenseImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
	
	@PostMapping("/cars")
	public Car saveCar(@RequestPart("car") String carJson, @RequestPart("file") MultipartFile file,
			@RequestPart("serviceFile") MultipartFile serviceFile) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Car car = objectMapper.readValue(carJson, Car.class);
		return carService.saveCar(car, file, serviceFile);
	}

	@GetMapping("/cars")
	public List<Car> getAllCars() {
		return carService.getAllCars();
	}

	@GetMapping("/cars/{id}")
	public Optional<Car> getCarById(@PathVariable("id") Long id) {
		return carService.getCarById(id);
	}

	@PostMapping("/cars/search")
	public ResponseEntity<List<CarDto>> searchCars(@RequestBody SearchCarDTO searchCarDTO) {
		try {

			List<CarDto> cars = carService.searchCars(searchCarDTO);

			return ResponseEntity.ok(cars);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/bookings/{id}/status")
	public BookCarDto updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
		BookCarStatus bookCarStatus;
		try {
			bookCarStatus = BookCarStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid status value: " + status);
		}
		BookCar updatedBookCar = bookingService.updateBookingStatus(id, bookCarStatus);
		return convertToDTO(updatedBookCar);
	}

	private BookCarDto convertToDTO(BookCar bookCar) {
		BookCarDto dto = new BookCarDto();
		dto.setId(bookCar.getId());
		dto.setFromDate(bookCar.getFromDate());
		dto.setToDate(bookCar.getToDate());
		dto.setDays(bookCar.getDays());
		dto.setPrice(bookCar.getPrice());
		dto.setBookCarStatus(bookCar.getBookCarStatus());
		dto.setUserId(bookCar.getUser().getId());
		dto.setCarId(bookCar.getCar().getId());
		return dto;
	}

	@GetMapping("/drivers")
	public List<Driver> getAllDrivers() {
		return driverService.getAllDrivers();
	}

	@GetMapping("/driver/{id}")
	public Driver getDriverById(@PathVariable Long id) {
		return driverService.getDriverById(id);
	}

	@PostMapping("/driver/hire")
	public ResponseEntity<Void> hireDriver(@RequestBody DriverDto driverDto) {
		boolean hired = driverService.hireDriver(driverDto);
		if (hired) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/hirings/{userId}")
	public ResponseEntity<List<DriverDto>> getHiringsByUserId(@PathVariable Long userId) {
		List<DriverDto> hirings = customerService.getHiringsByUserId(userId);
		return ResponseEntity.ok(hirings);
	}

	@PutMapping("/hirings/{id}/status")
	public DriverDto updateHiringStatus(@PathVariable Long id, @RequestParam String status) {
		HireDriverStatus hireDriverStatus;
		try {
			hireDriverStatus = HireDriverStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid status value: " + status);
		}
		HireDriver updatedHireDriver = driverService.updateBookingStatus(id, hireDriverStatus);
		return convertDriverToDTO(updatedHireDriver);
	}

	private DriverDto convertDriverToDTO(HireDriver hireDriver) {
		DriverDto dto = new DriverDto();
		dto.setId(hireDriver.getId());
		dto.setFromDate(hireDriver.getFromDate());
		dto.setToDate(hireDriver.getToDate());
		dto.setDays(hireDriver.getDays());
		dto.setHireStatus(hireDriver.getHireDriverStatus());
		dto.setUserId(hireDriver.getUser().getId());
		dto.setDriverId(hireDriver.getDriver().getId());
		return dto;
	}
	
	

	@PostMapping("/create_order")
	public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
		
	    BigDecimal amt = new BigDecimal(data.get("amount").toString());
	    int amountInPaise = amt.multiply(BigDecimal.valueOf(100)).intValue();

	    var client = new RazorpayClient(key, secret);

	    JSONObject orderRequest = new JSONObject();
	    orderRequest.put("amount", amountInPaise);
	    orderRequest.put("currency", "INR");
	    orderRequest.put("receipt", "receipt#1");
	    
	    Order order = client.orders.create(orderRequest);
	    System.out.println(order);
	    
	    // Save order info in db if necessary
	    
	    return ResponseEntity.ok(order.toString());
	}
	
	@GetMapping("/cars/{carId}/booked-dates")
    public ResponseEntity<List<BookCarDto>> getBookedDates(@PathVariable Long carId) {
        List<BookCarDto> bookedDates = bookingService.getBookedDates(carId);
        return ResponseEntity.ok(bookedDates);
    }
	
	@GetMapping("/driver/{driverId}/booked-dates")
	public ResponseEntity<List<DriverDto>> getHiredDates(@PathVariable Long driverId){
		List<DriverDto> hiredDates= driverService.getHiredDates(driverId);
		return ResponseEntity.ok(hiredDates);
	}
}
