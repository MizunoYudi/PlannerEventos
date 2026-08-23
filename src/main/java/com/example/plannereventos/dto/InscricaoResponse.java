package com.example.plannereventos.dto;

import com.example.plannereventos.model.Inscricao;
import java.time.LocalDateTime;

public class InscricaoResponse {
    private Long id;
    private Long eventoId;
    private Long participanteId;
    private LocalDateTime dataHoraInscricao;
    private String status;

    public InscricaoResponse() {
    }

    public InscricaoResponse(Long id, Long eventoId, Long participanteId, LocalDateTime dataHoraInscricao, String status) {
        this.id = id;
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.dataHoraInscricao = dataHoraInscricao;
        this.status = status;
    }

    public InscricaoResponse(Inscricao inscricao) {
        this.id = inscricao.getId();
        this.eventoId = inscricao.getIdEvento();
        this.participanteId = inscricao.getParticipanteId();
        this.dataHoraInscricao = inscricao.getDataInscricao();
        this.status = inscricao.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }

    public LocalDateTime getDataHoraInscricao() {
        return dataHoraInscricao;
    }

    public void setDataHoraInscricao(LocalDateTime dataHoraInscricao) {
        this.dataHoraInscricao = dataHoraInscricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}