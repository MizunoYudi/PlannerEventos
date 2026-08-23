package com.example.plannereventos.dto;

import com.example.plannereventos.model.Evento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoResponse {

    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioTermino;
    private String local;
    private int capacidade;
    private String status;
    private LocalDateTime registroCriacao;

    public EventoResponse(Evento evento) {
        this.id = evento.getId();
        this.titulo = evento.getTitulo();
        this.descricao = evento.getDescricao();
        this.data = evento.getData();
        this.horarioInicio = evento.getHorarioInicio();
        this.horarioTermino = evento.getHorarioTermino();
        this.local = evento.getLocal();
        this.capacidade = evento.getCapacidade();
        this.status = evento.getStatus();
        this.registroCriacao = evento.getRegistroCriacao();
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDate getData() { return data; }
    public LocalDateTime getHorarioInicio() { return horarioInicio; }
    public LocalDateTime getHorarioTermino() { return horarioTermino; }
    public String getLocal() { return local; }
    public int getCapacidade() { return capacidade; }
    public String getStatus() { return status; }
    public LocalDateTime getRegistroCriacao() { return registroCriacao; }
}