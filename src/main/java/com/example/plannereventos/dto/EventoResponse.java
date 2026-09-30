package com.example.plannereventos.dto;

import com.example.plannereventos.model.Evento;

import com.example.plannereventos.model.ModalidadeEvento;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventoResponse {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFim;
    private String local;
    private int capacidadeMaxima;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;
    private ModalidadeEvento modalidade;
    private Integer idadeMinima;

    public EventoResponse() {
    }

    public static EventoResponse fromEntity(Evento evento) {
        EventoResponse response = new EventoResponse();
        response.id = evento.getId();
        response.titulo = evento.getTitulo();
        response.descricao = evento.getDescricao();
        response.data = evento.getData();
        response.horaInicio = evento.getHoraInicio();
        response.horaFim = evento.getHoraFim();
        response.local = evento.getLocal();
        response.capacidadeMaxima = evento.getCapacidadeMaxima();
        response.status = evento.getStatus();
        response.criadoEm = evento.getCriadoEm();
        response.modalidade = evento.getModalidade();
        response.idadeMinima = evento.getIdadeMinima();
        return response;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
