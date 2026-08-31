package com.example.plannereventos.exception;

public class EventoJaIniciadoException extends RuntimeException {

    public EventoJaIniciadoException(int eventoId) {
        super("Nao e permitido realizar inscricoes no evento " + eventoId +
                ", pois ele ja foi iniciado.");
    }
}