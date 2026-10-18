package com.astrospace.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.astrospace.dto.Responses.Message;
import com.astrospace.util.ApiException;

/** Every error leaves the API as {"success": false, "message": "..."} (what errorHandler.js produced). */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Message> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new Message(false, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Message> handleUnexpected(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Message(false, "Internal Server Error"));
    }

    /** Spring MVC's own errors (malformed JSON, 404, 405, 415, ...) get the same body shape. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String message = status == null ? "Error" : status.getReasonPhrase();
        return ResponseEntity.status(statusCode).headers(headers).body(new Message(false, message));
    }
}
