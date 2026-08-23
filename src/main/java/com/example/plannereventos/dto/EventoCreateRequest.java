package com.example.plannereventos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoCreateRequest {

    @NotBlank(message = "O título não pode ficar em branco")
    private String titulo;

    @NotBlank(message = "A descrição não pode ficar em branco")
    private String descricao;

    @NotNull(message = "A data é obrigatória")
    @FutureOrPresent(message = "A data não pode ser anterior à de hoje")
    private LocalDate data;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalDateTime horarioInicio;

    @NotNull(message = "O horário de término é obrigatório")
    private LocalDateTime horarioTermino;

    private String local;

    @Positive(message = "A capacidade deve ser maior que 0")
    private int capacidade;

    @AssertTrue(message = "O horário de término deve ser posterior ao horário de início")
    public boolean isHorarioValido() {
        if (horarioInicio == null || horarioTermino == null) return true;
        return horarioTermino.isAfter(horarioInicio);
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalDateTime getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(LocalDateTime horarioInicio) { this.horarioInicio = horarioInicio; }
    public LocalDateTime getHorarioTermino() { return horarioTermino; }
    public void setHorarioTermino(LocalDateTime horarioTermino) { this.horarioTermino = horarioTermino; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
}