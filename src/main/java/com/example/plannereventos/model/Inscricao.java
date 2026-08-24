package com.example.plannereventos.model;
import java.time.LocalDateTime;
import java.util.UUID;

public class Inscricao {
    private int id;
    private int idEvento;
    private UUID participanteId;
    private LocalDateTime dataInscricao;
    private String status = "CONFIRMADA";

    public Inscricao(){
    }

    public Inscricao(int id, int idEvento, UUID participanteId, LocalDateTime dataInscricao, String status){
        this.id = id;
        this.dataInscricao = dataInscricao;
        this.idEvento = idEvento;
        this.participanteId = participanteId;
        this.status = status;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public int getId() {
        return id;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public UUID getParticipanteId() {
        return participanteId;
    }

    public String getStatus() {
        return status;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public void setId(int id) { this.id = id; }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public void setParticipanteId(UUID participanteId) {
        this.participanteId = participanteId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}