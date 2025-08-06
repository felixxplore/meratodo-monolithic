package com.felix.meratodo.exception;

public class UserNotInTeamException extends RuntimeException {
    public UserNotInTeamException(String message) {
        super(message);
    }
}
