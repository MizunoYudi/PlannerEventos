package com.example.plannereventos.dto;

import com.example.plannereventos.model.Participante;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParticipanteResponse {
    private UUID id;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    private String nomeCompleto;
    private String email;
    private LocalDateTime criadoEm;

    public ParticipanteResponse() {
    }

    public ParticipanteResponse(UUID id, String nomeCompleto, String email, LocalDateTime criadoEm) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.criadoEm = criadoEm;
    }

    public static ParticipanteResponse fromEntity(Participante participante) {
        return new ParticipanteResponse(
                participante.getId(),
                participante.getNomeCompleto(),
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
