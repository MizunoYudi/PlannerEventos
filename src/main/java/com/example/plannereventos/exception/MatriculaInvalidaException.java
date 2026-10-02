package com.example.plannereventos.exception;

public class MatriculaInvalidaException extends RuntimeException {
    public MatriculaInvalidaException() {
        super("Inscrição permitida apenas para alunos com matrícula ativa.");
    }
}