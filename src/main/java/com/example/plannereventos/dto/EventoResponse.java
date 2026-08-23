package com.example.plannereventos.dto;

import com.example.plannereventos.model.Evento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventoResponse {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalTime horarioInicio;
    private LocalTime horarioTermino;
    private String local;
    private int capacidadeMaxima;
    private String status;
    private LocalDateTime registroCriacao;

    public EventoResponse() {
    }

    public EventoResponse(Evento evento) {
        this.id = evento.getId();
        this.titulo = evento.getTitulo();
        this.descricao = evento.getDescricao();
        this.data = evento.getData();
        this.horarioInicio = LocalTime.from(evento.getHorarioInicio());
        this.horarioTermino = LocalTime.from(evento.getHorarioTermino());
        this.local = evento.getLocal();
        this.capacidadeMaxima = evento.getCapacidadeMaxima();
        this.status = evento.getStatus();
        this.registroCriacao = evento.getRegistroCriacao();
    }
}
