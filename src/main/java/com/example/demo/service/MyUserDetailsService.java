package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.MyUserDetails;
import com.example.demo.model.User;
import com.example.demo.repositry.UserRepositry;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepositry userRepositry;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        
        User user = userRepositry.findByName(name);
        
        if (user==null){
            throw new UsernameNotFoundException("user not found");
        }
        return new MyUserDetails(user);
        
    }

    
    
}
