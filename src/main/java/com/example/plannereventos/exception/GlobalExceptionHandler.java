package com.example.plannereventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EmailJaCadastradoException.class,
            InscricaoDuplicadaException.class,
            EventoSemVagasException.class,
            EventoCanceladoException.class,
            EventoJaIniciadoException.class,
            CancelamentoInscricaoInvalidoException.class
    })
    public ResponseEntity<String> handleRegraDeNegocio(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({
            ParticipanteNaoEncontradoException.class,
            EventoNaoEncontradoException.class,
            InscricaoNaoEncontradaException.class
    })
    public ResponseEntity<String> handleRecursoNaoEncontrado(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidacao(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }
}