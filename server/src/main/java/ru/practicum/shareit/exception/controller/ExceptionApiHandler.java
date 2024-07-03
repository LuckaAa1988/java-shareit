package ru.practicum.shareit.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(ItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage itemException(ItemException e) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationException(MethodArgumentNotValidException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(StateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage stateException(StateException e) {
        return new ErrorMessage(e.getMessage());
    }
}
