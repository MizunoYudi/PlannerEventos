package com.example.plannereventos.dto;

import jakarta.validation.constraints.Size;

public class InscricaoCancelarRequest {

    @Size(
        max = 255,
        message = "O motivo deve ter no maximo 255 caracteres"
    )
    private String motivoCancelamento;

    public InscricaoCancelarRequest() {
    }

    public InscricaoCancelarRequest(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}