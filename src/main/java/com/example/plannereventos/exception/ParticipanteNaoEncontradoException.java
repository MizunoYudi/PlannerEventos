package com.example.plannereventos.exception;

import java.util.UUID;

public class ParticipanteNaoEncontradoException extends RuntimeException {
    public ParticipanteNaoEncontradoException(UUID id) {
        super("Participante nao encontrado com o ID: " + id);
    }
}
