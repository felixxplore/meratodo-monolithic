package com.felix.meratodo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request){

        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse>  handleTeamNotFoundException(TeamNotFoundException ex , HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse>  handleProjectNotFoundException(ProjectNotFoundException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(),HttpStatus.NOT_FOUND, request.getRequestURI());
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(ProjectAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProjectAlreadyExistsException(ProjectAlreadyExistsException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    @ExceptionHandler(CanNotAssignOwnerRoleException.class)
    public ResponseEntity<ErrorResponse> handleCanNotAssignOwnerRoleException(CanNotAssignOwnerRoleException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    @ExceptionHandler(CanNotInviteAsOwnerException.class)
    public ResponseEntity<ErrorResponse> handleCanNotInviteAsOwnerException(CanNotInviteAsOwnerException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    @ExceptionHandler(CanNotRemoveTeamOwnerException.class)
    public ResponseEntity<ErrorResponse> handleCanNotRemoveTeamOwnerException(CanNotRemoveTeamOwnerException ex, HttpServletRequest request){
        return buildErrorResponse((ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI()));
    }

    @ExceptionHandler(UserNotInTeamException.class)
    public ResponseEntity<ErrorResponse> handleUserNotInTeamException(UserNotInTeamException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path) {

        ErrorResponse errorResponse=new ErrorResponse(status.value(), status.getReasonPhrase(),message, LocalDateTime.now(),path);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
