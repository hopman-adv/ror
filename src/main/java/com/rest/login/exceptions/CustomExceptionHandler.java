package com.rest.login.exceptions;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.rest.login.payload.response.ErrorResponse;
import com.rest.login.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static com.rest.login.enums.EResponses.VALIDATION_FAILED;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<MessageResponse> handleBadRequestException(MethodArgumentNotValidException ex) {
        FieldError fieldError = Objects.requireNonNull(ex.getBindingResult().getFieldError());

        String field = fieldError.getField();
        String cause =  fieldError.getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse
                (VALIDATION_FAILED.getMessage(), field, cause);

        return ResponseEntity.badRequest().body(new MessageResponse(errorResponse));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<MessageResponse> handleInternalException(ValidationException ex) {
//        TODO: Přidat specifická pole do response dle typu constraint exception.
//        if(ex instanceof ConstraintViolationException) {
//            Set<ConstraintViolation<?>> set = ((ConstraintViolationException) ex).getConstraintViolations();
//            Optional<ConstraintViolation<?>> constraintViolation = set.stream().map(i-> i.unwrap(ConstraintViolation)).findFirst();
//            String fieldName = constraintViolation.
//        }

        String field = ex.getLocalizedMessage();
        String cause =  ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse
                (VALIDATION_FAILED.getMessage(), field, cause);

        return ResponseEntity.badRequest().body(new MessageResponse(errorResponse));
    }

}
