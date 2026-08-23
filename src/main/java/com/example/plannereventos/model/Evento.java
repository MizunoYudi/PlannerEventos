package com.example.plannereventos.model;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Evento {
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

    @JsonCreator
    public Evento() {
    }

    public Evento(int id,
                  String titulo,
                  String descricao,
                  LocalDate data,
                  LocalDateTime horarioInicio,
                  LocalDateTime horarioTermino,
                  String local,
                  int capacidade,
                  String status,
                  LocalDateTime registroCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioTermino = horarioTermino;
        this.local = local;
        this.capacidade = capacidade;
        this.status = status;
        this.registroCriacao = registroCriacao;
    }

    public int getId() {
        return id;
    }

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

    /* public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }*/

    public LocalDateTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalDateTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalDateTime getHorarioTermino() {
        return horarioTermino;
    }

    public void setHorarioTermino(LocalDateTime horarioTermino) {
        this.horarioTermino = horarioTermino;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistroCriacao() {
        return registroCriacao;
    }

    public void setRegistroCriacao(LocalDateTime registroCriacao) {
        this.registroCriacao = registroCriacao;
    }
}
