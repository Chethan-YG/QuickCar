package com.quickcar.rent.service.customer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.dto.DriverDto;
import com.quickcar.rent.entity.BookCar;
import com.quickcar.rent.entity.Car;
import com.quickcar.rent.entity.HireDriver;
import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.BookCarStatus;
import com.quickcar.rent.repository.BookCarRepository;
import com.quickcar.rent.repository.CarRepository;
import com.quickcar.rent.repository.HireDriverRepository;
import com.quickcar.rent.repository.UserRepository;
import com.quickcar.rent.service.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	//private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private final CarRepository carRepository;
	private final UserRepository userRepository;
	private final BookCarRepository bookCarRepository;
	private final HireDriverRepository hireDriverRepository;
	private final MailService mailService;
	private final Cloudinary cloudinary;

	@Override
	@Transactional
	public boolean bookCar(BookCarDto bookCarDto,MultipartFile licenseImage) {
		Optional<Car> optionalCar = carRepository.findById(bookCarDto.getCarId());
		Optional<User> optionalUser = userRepository.findById(bookCarDto.getUserId());
		String url = "";
	    if (licenseImage != null && !licenseImage.isEmpty()) {
	        try {
	            Map<String, Object> uploadImageResult = cloudinary.uploader().upload(licenseImage.getBytes(), ObjectUtils.emptyMap());
	            url = uploadImageResult.get("url").toString();
	        } catch (IOException e) {
	        	System.out.println(e);
	        }
	    }

		if (optionalCar.isPresent() && optionalUser.isPresent()) {
			//log.info("Received BookCarDto: {}", bookCarDto);
			Car car = optionalCar.get();
			User user = optionalUser.get();
			BookCar bookCar = new BookCar();
			bookCar.setUser(user);
			bookCar.setCar(car);
			bookCar.setBookCarStatus(BookCarStatus.PENDING);
			bookCar.setFromDate(bookCarDto.getFromDate());
			bookCar.setToDate(bookCarDto.getToDate());
			long diffMilliseconds = bookCarDto.getToDate().getTime() - bookCarDto.getFromDate().getTime();
			long days = TimeUnit.MILLISECONDS.toDays(diffMilliseconds);
			bookCar.setDays(days);
			bookCar.setPrice(car.getPrice() * days);
			bookCar.setLicenseImage(url);
			bookCarRepository.save(bookCar);
			mailService.sendBookingStatusEmail(user.getEmail(), car, "Booked");
			return true;
		}
		return false;
	}

	@Override
	public List<BookCarDto> getBookingsByUserId(Long userId) {
		List<BookCar> bookings = bookCarRepository.findByUserId(userId);
		return bookings.stream().map(bookCar -> {
			BookCarDto dto = new BookCarDto();
			dto.setId(bookCar.getId());
			dto.setFromDate(bookCar.getFromDate());
			dto.setToDate(bookCar.getToDate());
			dto.setDays(bookCar.getDays());
			dto.setPrice(bookCar.getPrice());
			dto.setBookCarStatus(bookCar.getBookCarStatus());
			dto.setCarId(bookCar.getCar().getId());
			dto.setUserId(bookCar.getUser().getId());
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public Optional<User> getUserById(Long Id) {
		return userRepository.findById(Id);
	}

	@Override
	public List<DriverDto> getHiringsByUserId(Long userId) {
		List<HireDriver> hirings = hireDriverRepository.findByUserId(userId);
		return hirings.stream().map(hireDriver -> {
			DriverDto dto = new DriverDto();
			dto.setId(hireDriver.getId());
			dto.setFromDate(hireDriver.getFromDate());
			dto.setToDate(hireDriver.getToDate());
			dto.setDays(hireDriver.getDays());
			dto.setHireStatus(hireDriver.getHireDriverStatus());
			dto.setDriverId(hireDriver.getDriver().getId());
			dto.setUserId(hireDriver.getUser().getId());
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public Optional<User> findByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}
}
