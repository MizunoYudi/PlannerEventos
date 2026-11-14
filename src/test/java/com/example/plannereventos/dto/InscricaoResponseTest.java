package com.example.plannereventos.dto;

import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.StatusInscricao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InscricaoResponseTest {

    @Test
    @DisplayName("Deve mapear Inscricao com ComprovanteResponse e cobrir todos os construtores e getters/setters")
    void deveCobrirConstrutoresGettersESetters() {
        UUID participanteId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        Inscricao inscricao = new Inscricao(
                10,
                5,
                participanteId,
                agora,
                StatusInscricao.CONFIRMADA,
                LoteInscricao.LOTE_1,
                150.0,
                null
        );

        ComprovanteResponse comprovante = mock(ComprovanteResponse.class);
        InscricaoResponse comComprovante = new InscricaoResponse(inscricao, comprovante);

        assertEquals(10, comComprovante.getId());
        assertEquals(5, comComprovante.getEventoId());
        assertEquals(participanteId, comComprovante.getParticipanteId());
        assertEquals(agora, comComprovante.getDataCriacao());
        assertEquals("CONFIRMADA", comComprovante.getStatus());
        assertEquals(LoteInscricao.LOTE_1, comComprovante.getLoteAtivo());
        assertEquals(150.0, comComprovante.getValorFinal());
        assertNull(comComprovante.getMotivoCancelamento());
        assertEquals(comprovante, comComprovante.getComprovante());

        Inscricao inscricaoSemStatus = mock(Inscricao.class);
        when(inscricaoSemStatus.getMotivoCancelamento()).thenReturn("Motivo");
        InscricaoResponse semStatus = new InscricaoResponse(inscricaoSemStatus);
        assertNull(semStatus.getStatus());
        assertEquals("Motivo", semStatus.getMotivoCancelamento());

        InscricaoResponse legado = new InscricaoResponse(12, 6, participanteId, agora, "ATIVA", "Cancelado");
        assertEquals(12, legado.getId());
        assertEquals(6, legado.getEventoId());
        assertEquals("ATIVA", legado.getStatus());
        assertEquals("Cancelado", legado.getMotivoCancelamento());

        InscricaoResponse vazio = new InscricaoResponse();
        vazio.setId(20);
        vazio.setEventoId(8);
        vazio.setParticipanteId(participanteId);
        vazio.setNomeParticipante("Maria Silva");
        vazio.setDataCriacao(agora);
        vazio.setStatus("CANCELADA");
        vazio.setLoteAtivo(LoteInscricao.PROMOCIONAL);
        vazio.setValorFinal(75.0);
        vazio.setMotivoCancelamento("Imprevisto");
        vazio.setComprovante(comprovante);

        assertEquals(20, vazio.getId());
        assertEquals(8, vazio.getEventoId());
        assertEquals(participanteId, vazio.getParticipanteId());
        assertEquals("Maria Silva", vazio.getNomeParticipante());
        assertEquals(agora, vazio.getDataCriacao());
        assertEquals("CANCELADA", vazio.getStatus());
        assertEquals(LoteInscricao.PROMOCIONAL, vazio.getLoteAtivo());
        assertEquals(75.0, vazio.getValorFinal());
        assertEquals("Imprevisto", vazio.getMotivoCancelamento());
        assertEquals(comprovante, vazio.getComprovante());
    }
}