package com.events.application.jwt;

import com.events.application.model.UserEntity;
import com.events.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class EventUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = usersRepo.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User not found");
        }
        String role = user.getRole();
        if (role == null || role.isEmpty()) {
            throw new IllegalStateException("User role cannot be null or empty");
        }


        return new EventUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
