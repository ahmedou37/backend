package com.example.demo.managers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.example.demo.providers.AuthProvider;

import jakarta.annotation.PostConstruct;

@Component
public class AuthManager implements AuthenticationManager {

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private List<AuthenticationProvider> providers;
    
    @PostConstruct
    public void init() {
        this.providers.add(this.authProvider); //Now both are injected
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        for (AuthenticationProvider provider : providers) {
            if (provider.supports(authentication.getClass())) {
                Authentication result = provider.authenticate(authentication);
                if (result != null) {
                    return result;
                }
            }
        }
        throw new AuthenticationException("No suitable authentication provider found") {};
    }

    
}
