package com.example.plannereventos.exception;

public class EventoSemVagasException extends RuntimeException {

    public EventoSemVagasException(int eventoId) {
        super("O evento " + eventoId + " nao possui vagas disponiveis.");
    }
}