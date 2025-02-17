package com.events.application.controller;

import com.events.application.dto.LoginDTO;
import com.events.application.model.UserEntity;
import com.events.application.repository.UserRepository;
import com.events.application.service.RegistrationAndLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationAndLoginService registrationAndLoginService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
       return registrationAndLoginService.registerUser(user);
    }

    @PostMapping("/loginUser")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        return registrationAndLoginService.loginUser(loginDTO);
    }


}
