package com.felix.meratodo.service;

public class PasswordValidator {
    public static void validate(String password){
        if(password == null || password.length()<8) throw  new IllegalArgumentException("Password must be at least 8 characters");
        if(!password.matches(".*[A-Z].*")) throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        if(!password.matches(".*[a-z].*")) throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        if(!password.matches(".*[0-9].*")) throw new IllegalArgumentException("Password must contain at least one digit");
        if(!password.matches(".*[!@#$%^&*].*")) throw new IllegalArgumentException("Password must contain at least one special character");

    }
}
