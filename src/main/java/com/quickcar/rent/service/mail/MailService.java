package com.quickcar.rent.service.mail;

import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.quickcar.rent.entity.CallForm;
import com.quickcar.rent.entity.Car;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Async
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(1000000);
        return String.format("%06d", otp);
    }

    @Async
    public void sendContactFormEmail(CallForm callForm) {
        String subject = "Customer Contact Form";
        String text = "Name: " + callForm.getName() + "\nPhone Number: " + callForm.getPhoneNumber();
        sendMail("chethangangadhar58@gmail.com", subject, text);
    }
    
    @Async
    public void sendBookingStatusEmail(String to, Car car, String status) {
        String subject = "Booking Status for Car: " + car.getBrand() + " " + car.getName();
        String text = "Dear Customer,\n\n"
                     + "Your booking status for the car " + car.getBrand() + " " + car.getName() + " is: " + status + ".\n\n"
                     + "If you have any questions, please contact us.\n\n"
                     + "Thank you for choosing QuickCar!\n"
                     + "Best regards,\n"
                     + "QuickCar Team";

        sendMail(to, subject, text);
    }
    
    @Async
    public void sendDriverStatusEmail(String to, String driverName, String status) {
        String subject = "Driver Status Update: " + driverName;
        String text = "Dear Customer,\n\n"
                     + "We would like to inform you about the status update for your hired driver, " + driverName + ".\n\n"
                     + "Current Status: " + status + ".\n\n"
                     + "If you have any questions or need further assistance, please contact us.\n\n"
                     + "Thank you for choosing QuickCar!\n"
                     + "Best regards,\n"
                     + "QuickCar Team";

        sendMail(to, subject, text);
    }

}
