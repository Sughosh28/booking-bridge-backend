package com.events.application.service;

import com.events.application.dto.LoginDTO;
import com.events.application.jwt.EventUserDetailService;
import com.events.application.jwt.JwtService;
import com.events.application.model.UserEntity;
import com.events.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationAndLoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EventUserDetailService eventUserDetailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;

    public ResponseEntity<?> registerUser(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            UserEntity newUser = new UserEntity();
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(encodedPassword);
            newUser.setPhone(user.getPhone());
            newUser.setRole(user.getRole());
            newUser.setAddress(user.getAddress());
            mailService.sendWelcomeMail(user.getEmail(), user.getUsername());
            userRepository.save(newUser);
            return new ResponseEntity<>("Registration Successful", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Registration failed", HttpStatus.OK);
        }
    }

    public ResponseEntity<?> loginUser(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password()));

            if (authentication != null && authentication.isAuthenticated()) {
                String token = jwtService.generateToken(eventUserDetailService.loadUserByUsername(loginDTO.username()));
                String role= eventUserDetailService.loadUserByUsername(loginDTO.username()).getAuthorities().iterator().next().getAuthority();
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login Successful");
                response.put("role", role);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found or authentication failed.", HttpStatus.NOT_FOUND);
            }
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
