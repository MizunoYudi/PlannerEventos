package com.example.plannereventos.dto;

import com.example.plannereventos.model.Participante;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ParticipanteResponse {
    private UUID id;
    private String nome;
    private String email;
    private String matricula;
    private Boolean matriculaAtiva;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;

    public ParticipanteResponse() {
    }

    public ParticipanteResponse(UUID id, String nome, String email, String matricula, Boolean matriculaAtiva, LocalDate dataNascimento, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.matriculaAtiva = matriculaAtiva;
        this.dataNascimento = dataNascimento;
        this.criadoEm = criadoEm;
    }

    public static ParticipanteResponse fromEntity(Participante participante) {
        ParticipanteResponse response = new ParticipanteResponse();
        response.id = participante.getId();
        response.nome = participante.getNome();
        response.email = participante.getEmail();
        response.matricula = participante.getMatricula();
        response.matriculaAtiva = participante.getMatriculaAtiva();
        response.dataNascimento = participante.getDataNascimento();
        response.criadoEm = participante.getCriadoEm();
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Boolean getMatriculaAtiva() {
        return matriculaAtiva;
    }

    public void setMatriculaAtiva(Boolean matriculaAtiva) {
        this.matriculaAtiva = matriculaAtiva;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}