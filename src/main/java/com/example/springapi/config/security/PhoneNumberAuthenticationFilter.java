package com.example.springapi.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class PhoneNumberAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public PhoneNumberAuthenticationFilter() {
        super(new AntPathRequestMatcher("/auth/phone", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // Implement phone number authentication logic here
        return null;
    }
}
