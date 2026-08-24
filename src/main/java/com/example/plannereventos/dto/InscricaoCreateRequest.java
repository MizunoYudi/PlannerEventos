package com.example.plannereventos.dto;

import java.util.UUID;

public class InscricaoCreateRequest {
    private UUID participanteId;

    public InscricaoCreateRequest() {
    }

    public InscricaoCreateRequest(UUID participanteId) {
        this.participanteId = participanteId;
    }

    public UUID getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(UUID participanteId) {
        this.participanteId = participanteId;
    }
}