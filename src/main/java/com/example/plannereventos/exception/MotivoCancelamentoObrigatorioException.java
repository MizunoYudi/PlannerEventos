package com.example.plannereventos.exception;

public class MotivoCancelamentoObrigatorioException extends RuntimeException {

    public MotivoCancelamentoObrigatorioException() {
        super("O motivo do cancelamento e obrigatorio.");
    }
}