package com.example.plannereventos.dto;

public class EventoVagasResponse {
    private int id;
    private int capacidadeMaxima;
    private int inscricoesConfirmadas;
    private int vagasDisponiveis;

    public EventoVagasResponse(int id, int capacidadeMaxima, int inscricoesConfirmadas, int vagasDisponiveis) {
        this.id = id;
        this.capacidadeMaxima = capacidadeMaxima;
        this.inscricoesConfirmadas = inscricoesConfirmadas;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public int getId() {
        return id;
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