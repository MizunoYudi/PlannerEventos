package com.example.plannereventos.exception;

public class IdadeNaoPermitidaException extends RuntimeException {
    public IdadeNaoPermitidaException(int idadeMinima) {
        super("O participante não possui a idade mínima exigida de " + idadeMinima + " anos.");
    }
}