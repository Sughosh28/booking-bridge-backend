package com.events.application.controller;

import com.events.application.dto.EmailDTO;
import com.events.application.jwt.JwtService;
import com.events.application.service.MailService;
import com.events.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MailService mailService;


    @PostMapping("/send-otp-to-new-mail")
    public ResponseEntity<?> sendOtpToNewMail(@RequestHeader("Authorization") String token,@RequestBody EmailDTO emailDTO) {
        if(token==null || !token.startsWith("Bearer")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        return mailService.sendOtp(token,emailDTO.getEmail());
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateUserEmail(@RequestHeader("Authorization") String token, @RequestBody String otp) {
        if(token==null || !token.startsWith("Bearer")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken = token.replace("Bearer ", "");
        return userService.validateOtpAndUpdateEmail(authToken,otp);
    }
}
