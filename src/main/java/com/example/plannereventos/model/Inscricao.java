package com.example.plannereventos.model;

import com.example.plannereventos.exception.StatusInvalidoException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;
import java.util.UUID;

public class Inscricao {
    private int id;
    private int idEvento;
    private UUID participanteId;
    private LocalDateTime dataCriacao;
    private StatusInscricao status = StatusInscricao.PENDENTE;
    private LoteInscricao loteAtivo;
    private double valorFinal;
    private String motivoCancelamento;

    @JsonCreator
    public Inscricao() {
    }

    public Inscricao(int id, int idEvento, UUID participanteId, LocalDateTime dataCriacao,
                     StatusInscricao status, LoteInscricao loteAtivo, double valorFinal, String motivoCancelamento) {
        this.id = id;
        this.idEvento = idEvento;
        this.participanteId = participanteId;
        this.dataCriacao = dataCriacao;
        this.status = status != null ? status : StatusInscricao.PENDENTE;
        this.loteAtivo = loteAtivo;
        this.valorFinal = valorFinal;
        this.motivoCancelamento = motivoCancelamento;
    }

    public void confirmar() {
        validarConfirmacaoPermitida();
        this.status = StatusInscricao.CONFIRMADA;
    }

    public void cancelar(String motivo) {
        validarCancelamentoPermitido();
        this.status = StatusInscricao.CANCELADA;
        this.motivoCancelamento = (motivo != null && !motivo.isBlank()) ? motivo.trim() : null;
    }

    public void validarCancelamentoPermitido() {
        if (this.status == StatusInscricao.CANCELADA) {
            throw new StatusInvalidoException("A inscrição já está cancelada.");
        }
    }

    private void validarConfirmacaoPermitida() {
        if (this.status != StatusInscricao.PENDENTE) {
            throw new StatusInvalidoException(
                    "Somente inscrições PENDENTE podem ser confirmadas. Status atual: " + this.status
            );
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public UUID getParticipanteId() { return participanteId; }
    public void setParticipanteId(UUID participanteId) { this.participanteId = participanteId; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public StatusInscricao getStatus() { return status; }
    public void setStatus(StatusInscricao status) { this.status = status; }

    public LoteInscricao getLoteAtivo() { return loteAtivo; }
    public void setLoteAtivo(LoteInscricao loteAtivo) { this.loteAtivo = loteAtivo; }

    public double getValorFinal() { return valorFinal; }
    public void setValorFinal(double valorFinal) { this.valorFinal = valorFinal; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
}