package com.example.plannereventos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventoUpdateRequest {

    @NotBlank(message = "O título não pode ficar em branco")
    private String titulo;

    @NotBlank(message = "A descrição não pode ficar em branco")
    private String descricao;

    @NotNull(message = "A data é obrigatória")
    @FutureOrPresent(message = "A data não pode ser anterior à de hoje")
    private LocalDate data;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalTime horaInicio;

    @NotNull(message = "O horário de término é obrigatório")
    private LocalTime horaFim;

    private String local;

    @Positive(message = "A capacidade deve ser maior que 0")
    private int capacidadeMaxima;

    @AssertTrue(message = "O horário de término deve ser posterior ao horário de início")
    public boolean isHorarioValido() {
        if (horaInicio == null || horaFim == null) return true;
        return horaFim.isAfter(horaInicio);
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
}