package com.events.application.controller;

import com.events.application.model.UserEntity;
import com.events.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/profile")
    public String profile(OAuth2AuthenticationToken token, org.springframework.ui.Model model) {
        model.addAttribute("name", token.getPrincipal().getAttribute("name"));
        model.addAttribute("email", token.getPrincipal().getAttribute("email"));
        model.addAttribute("photo", token.getPrincipal().getAttribute("picture"));
        String name=token.getPrincipal().getAttribute("name");
        String email=token.getPrincipal().getAttribute("email");
        UserEntity entity=userRepository.findByEmail(email);
        System.out.println(entity);
        if(entity==null) {
            UserEntity oAuthUser = new UserEntity();
            oAuthUser.setUsername(name);
            oAuthUser.setEmail(email);
            oAuthUser.setRole("USER");
            userRepository.save(oAuthUser);
        }
        else{
            return "email-exists";
        }
        return "login-success";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "welcome";
    }
}
