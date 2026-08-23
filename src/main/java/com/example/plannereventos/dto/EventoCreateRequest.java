package com.example.plannereventos.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventoCreateRequest {

    @NotBlank(message = "O titulo e obrigatorio")
    private String titulo;

    @NotBlank(message = "A descricao e obrigatoria")
    private String descricao;

    @NotNull(message = "A data e obrigatoria")
    @Future(message = "A data deve ser futura")
    private LocalDate data;

    @NotNull(message = "O horario de inicio e obrigatorio")
    private LocalTime horarioInicio;

    @NotNull(message = "O horario de termino e obrigatorio")
    private LocalTime horarioTermino;

    @NotBlank(message = "O local e obrigatorio")
    private String local;

    @Min(value = 1, message = "A capacidade maxima deve ser maior que 0")
    private int capacidadeMaxima;

    public EventoCreateRequest() {
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

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioTermino() {
        return horarioTermino;
    }

    public void setHorarioTermino(LocalTime horarioTermino) {
        this.horarioTermino = horarioTermino;
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