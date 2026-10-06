package com.example.plannereventos.dto;

import com.example.plannereventos.model.TipoComprovante;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ComprovanteResponse {

    private final int inscricaoId;
    private final int eventoId;
    private final UUID participanteId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime dataInscricao;

    protected ComprovanteResponse(int inscricaoId, int eventoId, UUID participanteId, LocalDateTime dataInscricao) {
        this.inscricaoId = inscricaoId;
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.dataInscricao = dataInscricao;
    }

    public abstract TipoComprovante getTipo();

    public int getInscricaoId() {
        return inscricaoId;
    }

    public int getEventoId() {
        return eventoId;
    }

    public UUID getParticipanteId() {
        return participanteId;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }
}