package com.erasm.core.security.service;

import com.erasm.core.entity.User;
import com.erasm.core.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            String roleNameStr = user.getRole().getRoleName().name();
            if (!roleNameStr.startsWith("ROLE_")) {
                roleNameStr = "ROLE_" + roleNameStr;
            }
            authorities = List.of(new SimpleGrantedAuthority(roleNameStr));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
