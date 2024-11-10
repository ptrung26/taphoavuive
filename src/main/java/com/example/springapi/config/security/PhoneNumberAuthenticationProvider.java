package com.example.springapi.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PhoneNumberAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public PhoneNumberAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getName();

        // Replace this logic with actual phone number verification (e.g., a call to a third-party API).
        if (!verifyPhoneNumber(phoneNumber)) {
            return null; // Authentication failed
        }

        // If verification is successful, load the user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        // Create and return an authenticated token
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                authentication.getCredentials(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Support UsernamePasswordAuthenticationToken (or a custom token if you have one)
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean verifyPhoneNumber(String phoneNumber) {
        // Implement the actual phone number verification logic here
        // For now, assume it's always verified for demonstration purposes
        return true;
    }
}
