package com.example.plannereventos.exception;

public class CancelamentoForaDoPrazoException extends RuntimeException {

    public CancelamentoForaDoPrazoException() {
        super("O cancelamento esta fora do prazo permitido.");
    }
}
