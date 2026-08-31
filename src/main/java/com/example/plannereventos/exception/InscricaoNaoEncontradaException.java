package com.example.plannereventos.exception;

import java.util.UUID;

public class InscricaoNaoEncontradaException extends RuntimeException {

    public InscricaoNaoEncontradaException(int eventoId, UUID participanteId) {
        super("Inscricao nao encontrada para o participante " + participanteId +
                " no evento " + eventoId);
    }
}