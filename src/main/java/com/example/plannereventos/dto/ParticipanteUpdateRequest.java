package com.example.plannereventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public class ParticipanteUpdateRequest {

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "O e-mail deve possuir um formato valido")
    private String email;

    private String matricula;

    private Boolean matriculaAtiva;

    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate dataNascimento;

    public ParticipanteUpdateRequest() {
    }

    public ParticipanteUpdateRequest(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public ParticipanteUpdateRequest(String nome, String email, String matricula, Boolean matriculaAtiva, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.matriculaAtiva = matriculaAtiva;
        this.dataNascimento = dataNascimento;
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
}