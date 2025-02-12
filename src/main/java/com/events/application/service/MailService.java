package com.events.application.service;

import com.events.application.jwt.JwtService;
import com.events.application.model.BookingEntity;
import com.events.application.model.UserEntity;
import com.events.application.repository.BookingRepository;
import com.events.application.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    public void sendWelcomeMail(String email, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Welcome to Event Management System");
        helper.setTo(email);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        context.setVariable("username", username);
        String htmlContent = templateEngine.process("welcome", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendBookingConfirmationMail(String email, String eventName, String location, LocalTime checkIn, int noOfTickets, double totalPrice, Long bookingId, LocalDate eventDate, LocalTime eventTime) throws MessagingException {
        String username=userRepository.findByEmail(email).getUsername();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Booking Confirmation");
        helper.setTo(email);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        context.setVariable("eventName", eventName);
        context.setVariable("username", username);
        context.setVariable("noOfTickets", noOfTickets);
        context.setVariable("location", location);
        context.setVariable("totalPrice", totalPrice);
        context.setVariable("bookingId", bookingId);
        context.setVariable("eventDate", eventDate);
        context.setVariable("eventTime", eventTime);
        context.setVariable("checkInTime", checkIn);
        String htmlContent = templateEngine.process("booking_success", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendBookingFailureMail(String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Booking Failure");
        helper.setTo(email);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        String htmlContent = templateEngine.process("booking_failure", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendBookingCancellationMail(String email, Long bookingId) throws MessagingException {
        Optional<BookingEntity> booking = bookingRepository.findById(bookingId);
        BookingEntity booked=booking.get();
        LocalDate eventDate=booked.getEvent().getEvent_date();
        LocalTime eventTime=booked.getEvent().getEvent_time();
        String eventName=booked.getEvent().getEvent_name();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Booking Failure");
        helper.setTo(email);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        context.setVariable("bookingId", bookingId);
        context.setVariable("eventName", eventName);
        context.setVariable("eventDate", eventDate);
        context.setVariable("eventTime", eventTime);
        String htmlContent = templateEngine.process("booking_cancellation", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendEventCancellationMail(String email, String eventName, LocalDate eventDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Event Cancellation");
        helper.setTo(email);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        context.setVariable("eventName", eventName);
        context.setVariable("eventDate", eventDate);
        String htmlContent = templateEngine.process("event_cancellation.html", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }


    public ResponseEntity<?> sendOtp(String token,String email) {
        String username;
        try {
            String authToken = token.replace("Bearer ", "");
            username = jwtService.extractUsername(authToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if(user.getEmail().equals(email)){
            return new ResponseEntity<>("User already exists with this email", HttpStatus.BAD_REQUEST);
        }
        String otp = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        user.setOtp(otp);
        user.setOtp_expiry(LocalDateTime.now().plusMinutes(5));
        user.setPending_email(email);
        userRepository.save(user);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("OTP for email verification");
        message.setFrom("sughoshathreya1@gmail.com");
        message.setTo(String.valueOf(email));
        message.setReplyTo("sughoshathreya1@gmail.com");
        message.setText("Your OTP for email verification is " + otp);
        mailSender.send(message);
        return new ResponseEntity<>("OTP has been sent to your entered email", HttpStatus.OK);

    }


    public void sendMailUpdated(String newEmail, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sughoshathreya1@gmail.com");
        helper.setSubject("Email Update Confirmation");
        helper.setTo(newEmail);
        helper.setReplyTo("sughoshathreya1@gmail.com");
        Context context = new Context();
        context.setVariable("email", newEmail);
        context.setVariable("username", username);
        String htmlContent = templateEngine.process("email_update.html", context);
        helper.setText(htmlContent, true);
        mailSender.send(message);

    }
}
