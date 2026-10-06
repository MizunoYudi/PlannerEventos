package com.example.plannereventos.dto;

import com.example.plannereventos.model.ModalidadeEvento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalTime horaInicio;

    @NotNull(message = "O horario de termino e obrigatorio")
    private LocalTime horaFim;

    @NotBlank(message = "O local e obrigatorio")
    private String local;

    @Min(value = 1, message = "A capacidade maxima deve ser maior que 0")
    private int capacidadeMaxima;

    private ModalidadeEvento modalidade = ModalidadeEvento.ABERTO;

    @Min(value = 0, message = "A idade minima nao pode ser negativa")
    private Integer idadeMinima;

    @AssertTrue(message = "O horario de termino deve ser posterior ao horario de inicio")
    public boolean isHorarioValido() {
        if (horaInicio == null || horaFim == null) return true;
        return horaFim.isAfter(horaInicio);
    }

    @AssertTrue(message = "A idade minima e obrigatoria e deve ser maior que 0 para eventos com restricao de idade")
    public boolean isIdadeMinimaValida() {
        if (modalidade != ModalidadeEvento.RESTRICAO_IDADE) return true;
        return idadeMinima != null && idadeMinima > 0;
    }

    public EventoCreateRequest() {
    }

    public EventoCreateRequest(String titulo,
                               String descricao,
                               LocalDate data,
                               LocalTime horaInicio,
                               LocalTime horaFim,
                               String local,
                               int capacidadeMaxima,
                               ModalidadeEvento modalidade,
                               Integer idadeMinima) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.modalidade = modalidade != null ? modalidade : ModalidadeEvento.ABERTO;
        this.idadeMinima = idadeMinima;
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

    public ModalidadeEvento getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEvento modalidade) {
        this.modalidade = modalidade;
    }

    public Integer getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(Integer idadeMinima) {
        this.idadeMinima = idadeMinima;
    }
}