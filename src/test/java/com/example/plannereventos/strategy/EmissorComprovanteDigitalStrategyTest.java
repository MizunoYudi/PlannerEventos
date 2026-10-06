package com.example.plannereventos.strategy;

import com.example.plannereventos.dto.ComprovanteDigitalResponse;
import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.QrCode;
import com.example.plannereventos.model.TipoComprovante;
import com.example.plannereventos.service.QrCodeHashService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmissorComprovanteDigitalStrategyTest {

    private final EmissorComprovanteDigitalStrategy emissor =
            new EmissorComprovanteDigitalStrategy(new QrCodeHashService());

    private Evento criarEvento() {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Tech Summit 2026");
        evento.setData(LocalDate.of(2026, 11, 20));
        evento.setHoraInicio(LocalTime.of(10, 0));
        evento.setHoraFim(LocalTime.of(14, 0));
        evento.setLocal("Auditorio Principal");
        evento.setModalidade(ModalidadeEvento.EXCLUSIVO_ALUNOS);
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
    @DisplayName("Deve atender eventos EXCLUSIVO_ALUNOS e RESTRICAO_IDADE")
    void deveAtenderEventosExclusivosEComRestricao() {
        assertEquals(
                Set.of(ModalidadeEvento.EXCLUSIVO_ALUNOS, ModalidadeEvento.RESTRICAO_IDADE),
                emissor.getModalidades());
    }

    @Test
    @DisplayName("Deve declarar a capacidade de gerar QR Code")
    void deveDeclararCapacidadeDeGerarQrCode() {
        assertTrue(emissor instanceof GeradorQrCode);
    }

    @Test
    @DisplayName("Deve emitir comprovante digital com dados detalhados do evento")
    void deveEmitirComprovanteDigitalCompleto() {
        UUID participanteId = UUID.randomUUID();

        ComprovanteResponse resposta = emissor.emitir(
                criarEvento(), criarParticipante(participanteId), criarInscricao(participanteId));

        assertInstanceOf(ComprovanteDigitalResponse.class, resposta);
        ComprovanteDigitalResponse comprovante = (ComprovanteDigitalResponse) resposta;
        assertEquals(TipoComprovante.DIGITAL_COMPLETO, comprovante.getTipo());
        assertEquals("Carlos Silva", comprovante.getNomeParticipante());
        assertEquals("Tech Summit 2026", comprovante.getTituloEvento());
        assertEquals(LocalDate.of(2026, 11, 20), comprovante.getDataEvento());
        assertEquals(LocalTime.of(10, 0), comprovante.getHoraInicio());
        assertEquals(LocalTime.of(14, 0), comprovante.getHoraFim());
        assertEquals("Auditorio Principal", comprovante.getLocalEvento());
        assertEquals(ModalidadeEvento.EXCLUSIVO_ALUNOS, comprovante.getModalidade());
        assertNotNull(comprovante.getQrCode());
    }

    @Test
    @DisplayName("Payload do QR Code deve conter evento, participante e o hash (RN06)")
    void payloadDeveConterEventoParticipanteEHash() {
        UUID participanteId = UUID.randomUUID();

        QrCode qrCode = emissor.gerarQrCode(
                criarEvento(), criarParticipante(participanteId), criarInscricao(participanteId));

        assertTrue(qrCode.payload().contains("evento=1"));
        assertTrue(qrCode.payload().contains("participante=" + participanteId));
        assertTrue(qrCode.payload().contains("hash=" + qrCode.hash()));
    }

    @Test
    @DisplayName("Mesma inscricao deve gerar sempre o mesmo QR Code (imutabilidade)")
    void mesmaInscricaoDeveGerarMesmoQrCode() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento();
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarInscricao(participanteId);

        assertEquals(
                emissor.gerarQrCode(evento, participante, inscricao),
                emissor.gerarQrCode(evento, participante, inscricao));
    }
}