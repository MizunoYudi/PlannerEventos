package com.example.plannereventos.exception;

import com.example.plannereventos.dto.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorMessage> handleEmailJaCadastrado(EmailJaCadastradoException ex, HttpServletRequest request) {
        ErrorMessage errorResponse = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                "Regra de Negocio Violada",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorMessage errorResponse = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validacao",
                erros,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ParticipanteNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> handleParticipanteNaoEncontrado(ParticipanteNaoEncontradoException ex, HttpServletRequest request) {
        ErrorMessage errorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Nao Encontrado",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EventoNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> handleEventoNaoEncontrado(EventoNaoEncontradoException ex, HttpServletRequest request) {
        ErrorMessage errorResponse = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Nao Encontrado",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
