package com.felix.meratodo.exception;

public class UserMfaNotEnabledException extends RuntimeException {
    public UserMfaNotEnabledException(String message) {
        super(message);
    }
}
