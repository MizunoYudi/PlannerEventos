package com.example.plannereventos.model;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Evento {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String local;
    private int capacidadeMaxima;
    private String status;
    private LocalDateTime criadoEm;
    private ModalidadeEvento modalidade = ModalidadeEvento.ABERTO;
    private Integer idadeMinima;

    @JsonCreator
    public Evento() {
    }

    public Evento(int id,
                  String titulo,
                  String descricao,
                  LocalDate data,
                  LocalTime horaInicio,
                  LocalTime horaFim,
                  String local,
                  int capacidadeMaxima,
                  String status,
                  LocalDateTime criadoEm,
                  ModalidadeEvento modalidade,
                  Integer idadeMinima) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.status = status;
        this.criadoEm = criadoEm;
        this.modalidade = modalidade != null ? modalidade : ModalidadeEvento.ABERTO;
        this.idadeMinima = idadeMinima;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
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

    public LocalTime getHoraInicio() { return horaInicio; }

    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }

    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

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
