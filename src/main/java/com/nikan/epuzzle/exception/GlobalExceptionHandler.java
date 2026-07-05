package com.nikan.epuzzle.exception;

import com.nikan.epuzzle.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        logger.error("CustomException caught: {}", ex.getMessageCode().name());

        String message = messageSource.getMessage(
                ex.getMessageCode().name(),
                null,
                ex.getMessageCode().name(),
                LocaleContextHolder.getLocale()
        );

        logger.error("Message from properties: {}", message);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessageCode().name(),
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("RuntimeException caught: {}", ex.getMessage(), ex);

        String message = messageSource.getMessage(
                MessageCode.UNKNOWN_ERROR.name(),
                null,
                "Unknown error occurred",
                LocaleContextHolder.getLocale()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                MessageCode.UNKNOWN_ERROR.name(),
                message
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}