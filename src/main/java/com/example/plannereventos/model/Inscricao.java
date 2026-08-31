package com.example.plannereventos.model;
import java.time.LocalDateTime;
import java.util.UUID;

public class Inscricao {
    private int id;
    private int idEvento;
    private UUID participanteId;
    private LocalDateTime dataCriacao;
    private String status = "CONFIRMADA";

    @JsonCreator
    public Inscricao(){
    }

    public Inscricao(int id, int idEvento, UUID participanteId, LocalDateTime dataCriacao, String status){
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.idEvento = idEvento;
        this.participanteId = participanteId;
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
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

    public void setdataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
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