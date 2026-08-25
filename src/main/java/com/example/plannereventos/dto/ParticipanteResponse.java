package com.example.plannereventos.dto;

import com.example.plannereventos.model.Participante;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParticipanteResponse {
    private UUID id;

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

    private String nome;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;

    public ParticipanteResponse() {
    }

    public ParticipanteResponse(UUID id, String nome, String email, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.criadoEm = criadoEm;
    }

    public static ParticipanteResponse fromEntity(Participante participante) {
        return new ParticipanteResponse(
                participante.getId(),
                participante.getNome(),
                participante.getEmail(),
                participante.getCriadoEm()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
