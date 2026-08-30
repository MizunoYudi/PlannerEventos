package com.example.plannereventos.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventoCreateRequest {
    @NotBlank(message = "Titulo não informado")
    private String titulo;

    @NotBlank(message = "Descricao não informada")
    private String descricao;

    @NotNull(message = "Data não informada")
    @Future(message = "Data Inválida, deve ser posterior ao dia atual")
    private LocalDate data;

    @NotNull(message = "Hora de inicio não informado")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim não informado")
    private LocalTime horaFim;

    @NotBlank(message = "Local não informado")
    private String local;

    @Min(value = 1, message = "Capacidade Inválida, deve ser maior que 0")
    private int capacidadeMaxima;

    @AssertTrue(message = "A hora de fim deve ser posterior a hora de início do evento")
    public boolean isHorarioValido() {
        if (horaInicio == null || horaFim == null) return true;
        return horaFim.isAfter(horaInicio);
    }

    public EventoCreateRequest() {
    }

    public EventoCreateRequest(String titulo,
                               String descricao,
                               LocalDate data,
                               LocalTime horaInicio,
                               LocalTime horaFim,
                               String local,
                               int capacidadeMaxima) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
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
}