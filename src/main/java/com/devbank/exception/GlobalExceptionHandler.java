package com.devbank.exception;

import com.devbank.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return new ErroResponse(404, ex.getMessage(), List.of(), LocalDateTime.now());
    }

    @ExceptionHandler(ConflitoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse tratarConflito(ConflitoException ex) {
        return new ErroResponse(409, ex.getMessage(), List.of(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return new ErroResponse(400, "Dados inválidos", erros, LocalDateTime.now());

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse tratarJsonInvalido(HttpMessageNotReadableException ex) {
        return new ErroResponse(400, "Corpo da requisição inválido", List.of(), LocalDateTime.now());
    }
}