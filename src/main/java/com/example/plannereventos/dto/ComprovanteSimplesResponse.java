package com.example.plannereventos.dto;

import com.example.plannereventos.model.TipoComprovante;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComprovanteSimplesResponse extends ComprovanteResponse {

    private final String resumo;

    public ComprovanteSimplesResponse(int inscricaoId, int eventoId, UUID participanteId,
                                      LocalDateTime dataInscricao, String resumo) {
        super(inscricaoId, eventoId, participanteId, dataInscricao);
        this.resumo = resumo;
    }

    @Override
    public TipoComprovante getTipo() {
        return TipoComprovante.SIMPLES;
    }

    public String getResumo() {
        return resumo;
    }
}