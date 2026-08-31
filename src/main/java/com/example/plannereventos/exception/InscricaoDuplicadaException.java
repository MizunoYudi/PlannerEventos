package com.example.plannereventos.exception;

import java.util.UUID;

public class InscricaoDuplicadaException extends RuntimeException {

    public InscricaoDuplicadaException(int eventoId, UUID participanteId) {
        super("O participante " + participanteId +
                " ja possui uma inscricao confirmada no evento " + eventoId);
    }
}