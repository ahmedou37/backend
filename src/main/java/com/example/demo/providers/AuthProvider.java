package com.example.demo.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.MyUserDetails;
import com.example.demo.service.MyUserDetailsService;


@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(authentication.getName());
        if (passwordEncoder.matches(authentication.getCredentials().toString(),userDetails.getPassword())){
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new UsernamePasswordAuthenticationToken(userDetails, null , userDetails.getAuthorities());
        }  
        throw new BadCredentialsException("invalid username or password");      
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
   
}