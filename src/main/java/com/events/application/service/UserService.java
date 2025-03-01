package com.events.application.service;

import com.events.application.jwt.EventUserDetailService;
import com.events.application.jwt.JwtService;
import com.events.application.model.UserEntity;
import com.events.application.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private EventUserDetailService eventUserDetailService;
    @Autowired
    private JwtService jwtService;

    public ResponseEntity<?> validateOtpAndUpdateEmail(String token, String otp) throws MessagingException {

        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }

        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }

        if (user.getOtp_expiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired.");
        }

        String newEmail = user.getPending_email();
        if (newEmail == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No new email provided.");
        }

        user.setEmail(newEmail);
        user.setPending_email(null);
        user.setOtp(null);
        user.setOtp_expiry(null);
        mailService.sendMailUpdated(newEmail, username);
        userRepository.save(user);

        return ResponseEntity.ok("Email updated successfully.");
    }



}
