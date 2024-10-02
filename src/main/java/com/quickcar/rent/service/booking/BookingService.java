package com.quickcar.rent.service.booking;

import org.springframework.stereotype.Service;

import com.quickcar.rent.dto.BookCarDto;
import com.quickcar.rent.entity.BookCar;
import com.quickcar.rent.entity.Car;
import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.BookCarStatus;
import com.quickcar.rent.repository.BookCarRepository;
import com.quickcar.rent.service.mail.MailService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookCarRepository bookCarRepository;
    private final MailService mailService;
    
    public List<BookCar> getAllBookCars() {
        return bookCarRepository.findAll(); 
    }
    
    public BookCar updateBookingStatus(Long id, BookCarStatus status) {
        BookCar bookCar = bookCarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        bookCar.setBookCarStatus(status);
        User user=bookCar.getUser();
        Car car=bookCar.getCar();
        bookCar = bookCarRepository.save(bookCar);
        mailService.sendBookingStatusEmail(user.getEmail(),car, status.toString().toLowerCase());
        return bookCar;
    }
    
    
    public List<BookCarDto> getBookedDates(Long carId) {
        List<BookCar> bookings = bookCarRepository.findByCarId(carId);
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private BookCarDto convertToDTO(BookCar bookCar) {
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
    }

}

