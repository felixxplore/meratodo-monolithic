package com.felix.meratodo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    int status;
    String error;
    String message;
    LocalDateTime timestamp;
    String path;
}
