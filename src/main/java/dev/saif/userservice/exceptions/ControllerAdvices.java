package dev.saif.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import dev.localservicesreview.ratingservice.dtos.ExceptionDto;

@ControllerAdvice
public class ControllerAdvices {
    @ExceptionHandler(EmailNotFoundException.class)
    private ResponseEntity<ExceptionDto> handleNotFoundException(EmailNotFoundException ex) {
        return new ResponseEntity(
                new ExceptionDto(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
