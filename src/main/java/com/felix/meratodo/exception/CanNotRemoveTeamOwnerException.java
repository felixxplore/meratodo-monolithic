package com.felix.meratodo.exception;

public class CanNotRemoveTeamOwnerException extends RuntimeException {
    public CanNotRemoveTeamOwnerException(String message) {
        super(message);
    }
}
