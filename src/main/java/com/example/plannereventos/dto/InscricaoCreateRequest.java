package com.example.plannereventos.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InscricaoCreateRequest {

    @NotNull(message = "O identificador do participante é obrigatorio")
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