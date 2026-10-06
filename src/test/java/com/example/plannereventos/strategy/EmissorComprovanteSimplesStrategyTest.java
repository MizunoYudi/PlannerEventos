package com.example.plannereventos.strategy;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.dto.ComprovanteSimplesResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.TipoComprovante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmissorComprovanteSimplesStrategyTest {

    private final EmissorComprovanteSimplesStrategy emissor = new EmissorComprovanteSimplesStrategy();

    private Evento criarEvento() {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Tech Summit 2026");
        evento.setData(LocalDate.of(2026, 11, 20));
        evento.setHoraInicio(LocalTime.of(10, 0));
        evento.setHoraFim(LocalTime.of(14, 0));
        evento.setLocal("Auditorio Principal");
        evento.setModalidade(ModalidadeEvento.ABERTO);
        return evento;
    }

    private Participante criarParticipante(UUID id) {
        Participante participante = new Participante();
        participante.setId(id);
        participante.setNome("Carlos Silva");
        return participante;
    }

    private Inscricao criarInscricao(UUID participanteId) {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(7);
        inscricao.setIdEvento(1);
        inscricao.setParticipanteId(participanteId);
        inscricao.setDataCriacao(LocalDateTime.of(2026, 10, 5, 9, 30));
        inscricao.setStatus("CONFIRMADA");
        return inscricao;
    }

    @Test
    @DisplayName("Deve atender somente eventos ABERTO")
    void deveAtenderSomenteEventoAberto() {
        assertEquals(Set.of(ModalidadeEvento.ABERTO), emissor.getModalidades());
    }

    @Test
    @DisplayName("Nao deve expor a capacidade de gerar QR Code (RN07)")
    void naoDeveExporGeracaoDeQrCode() {
        assertFalse(emissor instanceof GeradorQrCode);
    }

    @Test
    @DisplayName("Deve emitir comprovante simples com os dados comuns da inscricao")
    void deveEmitirComprovanteSimples() {
        UUID participanteId = UUID.randomUUID();

        ComprovanteResponse comprovante = emissor.emitir(
                criarEvento(), criarParticipante(participanteId), criarInscricao(participanteId));

        assertInstanceOf(ComprovanteSimplesResponse.class, comprovante);
        assertEquals(TipoComprovante.SIMPLES, comprovante.getTipo());
        assertEquals(7, comprovante.getInscricaoId());
        assertEquals(1, comprovante.getEventoId());
        assertEquals(participanteId, comprovante.getParticipanteId());
        assertEquals(LocalDateTime.of(2026, 10, 5, 9, 30), comprovante.getDataInscricao());
    }

    @Test
    @DisplayName("Resumo deve conter participante, evento, data, horario e local")
    void resumoDeveConterDadosDaInscricao() {
        UUID participanteId = UUID.randomUUID();

        ComprovanteSimplesResponse comprovante = (ComprovanteSimplesResponse) emissor.emitir(
                criarEvento(), criarParticipante(participanteId), criarInscricao(participanteId));

        String resumo = comprovante.getResumo();
        assertTrue(resumo.contains("Carlos Silva"));
        assertTrue(resumo.contains("Tech Summit 2026"));
        assertTrue(resumo.contains("20/11/2026"));
        assertTrue(resumo.contains("10:00"));
        assertTrue(resumo.contains("Auditorio Principal"));
    }
}