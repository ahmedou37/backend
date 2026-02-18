package com.example.demo.authorization.enums;

public enum Permission {
    USER("user"),
    ADMIN_READ("admin_read"),
    ADMIN_POST("admin_post"),
    ADMIN_DELETE("admin_delete"),
    ADMIN_PUT("admin_put");
    
    Permission(String permission) { this.permission = permission; }

    private final String permission;
  
    public String getPermission() {
       return this.permission;
    }
}
