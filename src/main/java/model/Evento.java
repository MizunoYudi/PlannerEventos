package model;
import java.time.LocalDateTime;
import java.util.Date;

public class Evento {
    private int id;
    private String titulo;
    private String descricao;
    private Date data;
    private LocalDateTime HorarioInicio;
    private LocalDateTime HorarioTermino;
    private String local;
    private int capacidade;
    private String status;
    private LocalDateTime registroCriacao;

    public Evento(int id,
                  String titulo,
                  String descricao,
                  Date data,
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
        HorarioInicio = horarioInicio;
        HorarioTermino = horarioTermino;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public LocalDateTime getHorarioInicio() {
        return HorarioInicio;
    }

    public void setHorarioInicio(LocalDateTime horarioInicio) {
        HorarioInicio = horarioInicio;
    }

    public LocalDateTime getHorarioTermino() {
        return HorarioTermino;
    }

    public void setHorarioTermino(LocalDateTime horarioTermino) {
        HorarioTermino = horarioTermino;
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
