package com.example.plannereventos.exception;

public class CancelamentoInscricaoInvalidoException extends RuntimeException {

    public CancelamentoInscricaoInvalidoException() {
        super("Apenas inscricoes confirmadas podem ser canceladas.");
    }
}