package com.example.departorium.service;

import com.example.departorium.dto.CustomUserDetails;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByUsername(username).orElse(null);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
