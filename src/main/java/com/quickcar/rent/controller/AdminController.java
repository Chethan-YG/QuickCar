package com.quickcar.rent.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.entity.BookCar;
import com.quickcar.rent.entity.Car;
import com.quickcar.rent.entity.Driver;
import com.quickcar.rent.entity.HireDriver;
import com.quickcar.rent.enums.BookCarStatus;
import com.quickcar.rent.enums.HireDriverStatus;
import com.quickcar.rent.service.car.CarService;
import com.quickcar.rent.service.driver.DriverService;

import lombok.RequiredArgsConstructor;

import com.quickcar.rent.service.booking.BookingService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {
	private final CarService carService;
	private final BookingService bookingService;
	private final DriverService driverService;
	private final RestTemplate restTemplate;
	
	@Value("${target.flask.endpoint}")
	private String targetFlaskEndpoint;

	@PostMapping("/cars")
	public Car saveCar(@RequestPart("car") String carJson, @RequestPart("file") MultipartFile file,
			@RequestPart("serviceFile") MultipartFile serviceFile) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Car car = objectMapper.readValue(carJson, Car.class);
		return carService.saveCar(car, file, serviceFile);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestPart("car") String carJson,
			@RequestPart(value = "file", required = false) MultipartFile file,
			@RequestPart(value = "serviceFile", required = false) MultipartFile serviceFile) {
		try {
			Car car = new ObjectMapper().readValue(carJson, Car.class);
			Car updatedCar = carService.updateCar(id, car, file, serviceFile);
			return ResponseEntity.ok(updatedCar);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid car data");
		}
	}

	@DeleteMapping("/cars/{id}")
	public ResponseEntity<String> deleteCar(@PathVariable Long id) throws Exception {
		Car deletedCar = carService.deleteCar(id);
		return ResponseEntity.ok("Car with id " + deletedCar.getId() + " deleted successfully");
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

	@GetMapping("/bookings")
	public List<BookCarDto> getAllBookCars() {
		List<BookCar> bookCars = bookingService.getAllBookCars();
		return bookCars.stream().map(this::convertToDTO).collect(Collectors.toList());
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
		dto.setLicenseImage(bookCar.getLicenseImage());
		return dto;
	}

	@PostMapping("/drivers")
	public Driver saveDriver(@RequestPart("driver") String driverJson, @RequestPart("file") MultipartFile file)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Driver driver = objectMapper.readValue(driverJson, Driver.class);
		return driverService.saveDriver(driver, file);
	}

	@PutMapping("/drivers/{id}")
	public ResponseEntity<Driver> updateDriver(@PathVariable Long id,
			@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("driver") String driverJson)
			throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		Driver driver = objectMapper.readValue(driverJson, Driver.class);

		Driver updatedDriver = driverService.updateDriver(id, driver, file);

		return ResponseEntity.ok(updatedDriver);
	}

	@GetMapping("/drivers/{id}")
	public Driver getDriverById(@PathVariable Long id) {
		return driverService.getDriverById(id);
	}

	@DeleteMapping("/drivers/{id}")
	public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
		driverService.deleteDriver(id);
		return ResponseEntity.ok("Driver with id " + id + " deleted successfully");
	}

	@GetMapping("/hirings")
	public List<DriverDto> getAllHiredDrivers() {
		List<HireDriver> hireDriver = driverService.getAllHiredDrivers();
		return hireDriver.stream().map(this::convertDriverToDTO).collect(Collectors.toList());
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

	@PostMapping("/processImage")
	public ResponseEntity<String> processImage(@RequestParam("file") MultipartFile file) throws IOException {
		// Get the original file name
		String fileName = file.getOriginalFilename();
		System.out.println(fileName);
		// Respond with a success message
		return ResponseEntity.ok("Image processed successfully");
	}

	@PostMapping("/damage/car")
	public ResponseEntity<String> uploadCarDamage(@RequestPart("file") MultipartFile file) throws IOException {
		String url = carService.uploadCarImage(file);
		System.out.println(url);

		String flaskUrl = targetFlaskEndpoint + "/receiveImage";
		ImagePayload payload = new ImagePayload(url);
		// ResponseEntity<String> response =
		restTemplate.postForEntity(flaskUrl, payload, String.class);

		return ResponseEntity.ok("");
	}

	// Helper class to structure the payload
	static class ImagePayload {
		private String imageUrl;

		public ImagePayload(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
	}

}
