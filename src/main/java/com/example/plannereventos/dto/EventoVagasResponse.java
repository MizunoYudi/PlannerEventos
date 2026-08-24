package com.example.plannereventos.dto;

public class EventoVagasResponse {
    private int eventoId;
    private int capacidadeMaxima;
    private int inscricoesConfirmadas;
    private int vagasDisponiveis;

    public EventoVagasResponse(int eventoId, int capacidadeMaxima, int inscricoesConfirmadas, int vagasDisponiveis) {
        this.eventoId = eventoId;
        this.capacidadeMaxima = capacidadeMaxima;
        this.inscricoesConfirmadas = inscricoesConfirmadas;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public int getEventoId() {
        return eventoId;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public int getInscricoesConfirmadas() {
        return inscricoesConfirmadas;
    }

    public int getVagasDisponiveis() {
        return vagasDisponiveis;
    }
}