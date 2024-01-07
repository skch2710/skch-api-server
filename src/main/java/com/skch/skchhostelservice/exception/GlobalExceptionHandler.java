package com.skch.skchhostelservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
//import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
//import org.springframework.security.web.csrf.InvalidCsrfTokenException;
//import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * The Class GlobalExceptionHandler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  
  /**
   * Custom exception handle.
   *
   * @param ex the ex
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> customExceptionHandle(CustomException ex, WebRequest request) {

    ErrorResponse response = new ErrorResponse();
    response.setStatusCode(ex.getStatus().value());
    response.setSuccessMessage(ex.getStatus().name());
    response.setErrorMessage(ex.getMessage());
    return new ResponseEntity<>(response, ex.getStatus());

  }
  
//  /**
//   * Access Denied Exception handle.
//   *
//   * @param edx the edx
//   * @param request the request
//   * @return the response entity
//   */
//  @ExceptionHandler(AccessDeniedException.class)
//  public ResponseEntity<?> accessDeniedException(AccessDeniedException edx) {
//
//    ErrorResponse response = new ErrorResponse();
//    response.setStatusCode(HttpStatus.FORBIDDEN.value());
//    response.setSuccessMessage("ACCESS_DENIED");
//    response.setErrorMessage(edx.getMessage());
//    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//  }
  
  /**
   * Access Denied Exception handle.
   *
   * @param edx the edx
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(Forbidden.class)
  public ResponseEntity<?> forbiddenException(Forbidden edx) {

    ErrorResponse response = new ErrorResponse();
    response.setStatusCode(HttpStatus.FORBIDDEN.value());
    response.setSuccessMessage("Forbidden");
    response.setErrorMessage(edx.getMessage());
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }
  
  
//  @ExceptionHandler(InvalidBearerTokenException.class)
//  public ResponseEntity<?> invalidBearerTokenException(InvalidBearerTokenException edx) {
//	    ErrorResponse response = new ErrorResponse();
//	    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//	    response.setSuccessMessage("The access token provided is expired, revoked, malformed, or invalid for other reasons.");
//	    response.setErrorMessage(edx.getMessage());
//	    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//	  }
//  
//  @ExceptionHandler(InsufficientAuthenticationException.class)
//  public ResponseEntity<?> insufficientAuthenticationException(InsufficientAuthenticationException edx) {
//	    ErrorResponse response = new ErrorResponse();
//	    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//	    response.setSuccessMessage("Full authentication is required to access this resource.");
//	    response.setErrorMessage(edx.getMessage());
//	    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//	  }
//  
//  @ExceptionHandler(MissingCsrfTokenException.class)
//  public ResponseEntity<?> insufficientAuthenticationException(MissingCsrfTokenException edx) {
//	    ErrorResponse response = new ErrorResponse();
//	    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//	    response.setSuccessMessage("Full authentication is required to access this resource.");
//	    response.setErrorMessage(edx.getMessage());
//	    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//	  }
//  
//  @ExceptionHandler(InvalidCsrfTokenException.class)
//  public ResponseEntity<?> invalidCsrfTokenException(InvalidCsrfTokenException edx) {
//	    ErrorResponse response = new ErrorResponse();
//	    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//	    response.setSuccessMessage("Full authentication is required to access this resource.");
//	    response.setErrorMessage(edx.getMessage());
//	    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//	  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> exception(Exception edx) {
	    ErrorResponse response = new ErrorResponse();
	    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
	    response.setSuccessMessage("Forbidden");
	    response.setErrorMessage(edx.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	  }
}
