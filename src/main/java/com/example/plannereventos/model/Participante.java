package com.example.plannereventos.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Participante {

    private UUID id;
    private String nome;
    private String email;
    private LocalDateTime criadoEm;

    public Participante() {
    }

    public Participante(String nome, String email) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.criadoEm = LocalDateTime.now();
    }

    public Participante(UUID id, String nome, String email, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.criadoEm = criadoEm;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participante that = (Participante) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Participante{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", criadoEm=" + criadoEm +
                '}';
    }
}