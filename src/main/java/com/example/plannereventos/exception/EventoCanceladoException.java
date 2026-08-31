package com.example.plannereventos.exception;

public class EventoCanceladoException extends RuntimeException {

    public EventoCanceladoException(int eventoId) {
        super("Nao e permitido realizar inscricoes no evento " + eventoId +
                ", pois ele esta cancelado.");
    }
}