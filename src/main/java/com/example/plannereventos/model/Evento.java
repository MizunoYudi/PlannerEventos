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
    private double valorBase = 0.0;
    private LoteInscricao loteAtivo = LoteInscricao.LOTE_2;

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
                  Integer idadeMinima,
                  double valorBase,
                  LoteInscricao loteAtivo) {
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
        this.valorBase = valorBase;
        this.loteAtivo = loteAtivo != null ? loteAtivo : LoteInscricao.LOTE_2;
    }

    public int getId() { return this.id; }
    public String getTitulo() { return this.titulo; }
    public String getDescricao() { return this.descricao; }
    public LocalDate getData() { return this.data; }
    public LocalTime getHoraInicio() { return this.horaInicio; }
    public LocalTime getHoraFim() { return this.horaFim; }
    public String getLocal() { return this.local; }
    public int getCapacidadeMaxima() { return this.capacidadeMaxima; }
    public String getStatus() { return this.status; }
    public LocalDateTime getCriadoEm() { return this.criadoEm; }
    public ModalidadeEvento getModalidade() { return this.modalidade; }
    public Integer getIdadeMinima() { return this.idadeMinima; }
    public double getValorBase() { return this.valorBase; }
    public LoteInscricao getLoteAtivo() { return this.loteAtivo; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setData(LocalDate data) { this.data = data; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public void setLocal(String local) { this.local = local; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public void setStatus(String status) { this.status = status; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public void setModalidade(ModalidadeEvento modalidade) { this.modalidade = modalidade; }
    public void setIdadeMinima(Integer idadeMinima) { this.idadeMinima = idadeMinima; }
    public void setValorBase(double valorBase) { this.valorBase = valorBase; }
    public void setLoteAtivo(LoteInscricao loteAtivo) { this.loteAtivo = loteAtivo; }
}