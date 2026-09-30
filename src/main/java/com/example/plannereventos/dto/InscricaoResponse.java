package com.example.plannereventos.dto;

import com.example.plannereventos.model.Inscricao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class InscricaoResponse {
    private int id;
    private int eventoId;
    private UUID participanteId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    private String status;
    private String motivoCancelamento;

    public InscricaoResponse() {
    }

    public InscricaoResponse(int id, int eventoId, UUID participanteId, LocalDateTime dataCriacao, String status, String motivoCancelamento) {
        this.id = id;
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.motivoCancelamento = motivoCancelamento;
    }

    public InscricaoResponse(Inscricao inscricao) {
        this.id = inscricao.getId();
        this.eventoId = inscricao.getIdEvento();
        this.participanteId = inscricao.getParticipanteId();
        this.dataCriacao = inscricao.getDataCriacao();
        this.status = inscricao.getStatus();
        this.motivoCancelamento = inscricao.getMotivoCancelamento();
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
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}