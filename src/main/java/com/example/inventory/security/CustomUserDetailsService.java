package com.example.inventory.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // For demo: single user "admin" / "password"
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.withUsername("admin")
                .password("{noop}password") // NOOP encoder just for assignment demo
                .authorities(List.of(() -> "ROLE_USER"))
                .build();
    }
}
