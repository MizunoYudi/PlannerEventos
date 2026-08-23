package com.example.plannereventos.model;
import java.time.LocalDateTime;

public class Inscricao {
    private Long id;
    private Long idEvento;
    private Long participanteId;
    private LocalDateTime dataInscricao;
    private String status = "CONFIRMADA";

    public Inscricao(){

    }

    public Inscricao(Long id, Long idEvento, Long participanteId, LocalDateTime dataInscricao, String status){
        this.id = id;
        this.dataInscricao = dataInscricao;
        this.idEvento = idEvento;
        this.participanteId = participanteId;
        this.status = status;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public Long getId() {
        return id;
    }

    public Long getIdEvento() {
        return idEvento;
    }

    public Long getParticipanteId() {
        return participanteId;
    }

    public String getStatus() {
        return status;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}