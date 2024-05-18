package ru.practicum.shareit.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(EmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage emailException(EmailException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(UpdateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage updateException(UpdateException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundException(NotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage accessDeniedException(AccessDeniedException e) {
        return new ErrorMessage(e.getMessage());
    }
}
