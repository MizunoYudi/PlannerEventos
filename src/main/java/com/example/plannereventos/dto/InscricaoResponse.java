package com.example.plannereventos.dto;

import com.example.plannereventos.model.Inscricao;
import java.time.LocalDateTime;
import java.util.UUID;

public class InscricaoResponse {
    private int id;
    private int eventoId;
    private UUID participanteId;
    private LocalDateTime dataCriacao;
    private String status;

    public InscricaoResponse() {
    }

    public InscricaoResponse(int id, int eventoId, UUID participanteId, LocalDateTime dataCriacao, String status) {
        this.id = id;
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.dataCriacao = dataCriacao;
        this.status = status;
    }

    public InscricaoResponse(Inscricao inscricao) {
        this.id = inscricao.getId();
        this.eventoId = inscricao.getIdEvento();
        this.participanteId = inscricao.getParticipanteId();
        this.DataCriacao = inscricao.getDataCriacaom();
        this.status = inscricao.getStatus();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public UUID getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(UUID participanteId) {
        this.participanteId = participanteId;
    }

    public LocalDateTime getDataCriacao() {
        return DataCriacao;
    }

    public void setDataCriacao(LocalDateTime DataCriacao) {
        this.DataCriacao = criadoEm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}