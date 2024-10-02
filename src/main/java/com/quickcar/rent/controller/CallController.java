package com.quickcar.rent.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quickcar.rent.entity.CallForm;
import com.quickcar.rent.service.mail.MailService;
import com.twilio.exception.TwilioException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/callback")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = RequestMethod.POST)
public class CallController {


    @Value("${spring.mail.username}")
    private String fromMail;
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;
    @Value("${twilio.auth.token}")
    private String twilioAuthToken;
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;
    
    private final MailService mailService;

    @PostMapping("/call")
    public ResponseEntity<?> submitContactForm(@RequestBody CallForm callForm) {
        try {
        	mailService.sendContactFormEmail(callForm);
            return ResponseEntity.ok(new ApiResponse("Success", "Contact form submitted successfully\n We'll get back to you soon"));
        } catch (MailException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", "Failed to send email"));
        } catch (TwilioException e) {
            System.out.println("Number not verified");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", "Failed to send SMS"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", "Failed to submit contact form"));
        }
    }

    

//    private void sendSms(CallForm callForm) {
//        Twilio.init(twilioAccountSid, twilioAuthToken);
//        Message.creator(
//                new PhoneNumber("+91" + callForm.getPhoneNumber()),
//                new PhoneNumber(twilioPhoneNumber),
//                "Hello " + callForm.getName() +
//                ", we've received your request for a callback and will be in touch soon. Thank you for choosing QuickCar! Best regards, QuickCar Team"
//        ).create();
//    }

       public static class ApiResponse {
        private String status;
        private String message;

        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}