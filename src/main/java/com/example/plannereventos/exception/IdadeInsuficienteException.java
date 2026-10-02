package com.example.plannereventos.exception;

public class IdadeInsuficienteException extends RuntimeException {
    public IdadeInsuficienteException(int idadeMinima) {
        super("O participante não possui a idade mínima exigida de " + idadeMinima + " anos.");
    }
}