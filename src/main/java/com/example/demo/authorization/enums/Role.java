package com.example.demo.authorization.enums;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER(Set.of(Permission.USER)),
    ADMIN(Set.of(Permission.USER, Permission.ADMIN_READ, Permission.ADMIN_POST, Permission.ADMIN_DELETE, Permission.ADMIN_PUT));

    private Set<Permission> permissions;

    private Role(Set<Permission> permissions) { this.permissions = permissions; } 
    
    public Set<Permission> getPermissions() { return this.permissions; }

    public List<SimpleGrantedAuthority> getAuthorities() { 
        List<SimpleGrantedAuthority> authorities = getPermissions().stream()
          .map((Permission permission) -> new SimpleGrantedAuthority(permission.getPermission()))
          .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
        }

}
