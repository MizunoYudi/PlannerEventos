package com.example.plannereventos.dto;

import java.util.UUID;

public class EventoVagasResponse {
    private UUID eventoId;
    private int capacidadeMaxima;
    private long inscricoesConfirmadas;
    private long vagasDisponiveis;

    public EventoVagasResponse(UUID eventoId, int capacidadeMaxima, long inscricoesConfirmadas, long vagasDisponiveis) {
        this.eventoId = eventoId;
        this.capacidadeMaxima = capacidadeMaxima;
        this.inscricoesConfirmadas = inscricoesConfirmadas;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public long getInscricoesConfirmadas() {
        return inscricoesConfirmadas;
    }

    public long getVagasDisponiveis() {
        return vagasDisponiveis;
    }
}
