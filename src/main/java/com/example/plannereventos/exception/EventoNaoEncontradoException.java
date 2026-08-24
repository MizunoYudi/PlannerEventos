package com.example.plannereventos.exception;

public class EventoNaoEncontradoException extends RuntimeException {
    public EventoNaoEncontradoException(int id) {
        super("Evento nao encontrado com o ID: " + id);
    }
}