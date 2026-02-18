package com.example.demo.authorization;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthPermission implements PermissionEvaluator{

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String target= (String) targetDomainObject;
        String action= (String) permission;

        if(target.equals("USER")&&action.equals("READ")){
            return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String targetTypeStr= (String) targetType;
        String action= (String) permission;
        int targetIdStr= (int) targetId;

        if(targetTypeStr.equals("USER")&&action.equals("READ")&& targetIdStr != 1){
            return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
    
}
