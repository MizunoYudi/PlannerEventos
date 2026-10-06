package com.example.plannereventos.service;

import com.example.plannereventos.dto.ComprovanteDigitalResponse;
import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.dto.ComprovanteSimplesResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.strategy.EmissorComprovanteDigitalStrategy;
import com.example.plannereventos.strategy.EmissorComprovanteSimplesStrategy;
import com.example.plannereventos.strategy.EmissorComprovanteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ComprovanteServiceTest {

    private ComprovanteService comprovanteService;
    private Participante participante;
    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        comprovanteService = new ComprovanteService(List.of(
                new EmissorComprovanteSimplesStrategy(),
                new EmissorComprovanteDigitalStrategy(new QrCodeHashService())
        ));

        participante = new Participante();
        participante.setId(UUID.randomUUID());
        participante.setNome("Carlos Silva");

        inscricao = new Inscricao();
        inscricao.setId(1);
        inscricao.setIdEvento(1);
        inscricao.setParticipanteId(participante.getId());
        inscricao.setDataCriacao(LocalDateTime.of(2026, 10, 5, 9, 30));
        inscricao.setStatus("CONFIRMADA");
    }

    private Evento criarEvento(ModalidadeEvento modalidade) {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Tech Summit 2026");
        evento.setData(LocalDate.of(2026, 11, 20));
        evento.setHoraInicio(LocalTime.of(10, 0));
        evento.setHoraFim(LocalTime.of(14, 0));
        evento.setLocal("Auditorio Principal");
        evento.setModalidade(modalidade);
        return evento;
    }

    @Test
    @DisplayName("Evento ABERTO deve gerar exclusivamente comprovante simples")
    void eventoAbertoDeveGerarComprovanteSimples() {
        ComprovanteResponse comprovante = comprovanteService.emitir(
                criarEvento(ModalidadeEvento.ABERTO), participante, inscricao);

        assertInstanceOf(ComprovanteSimplesResponse.class, comprovante);
    }

    @Test
    @DisplayName("Evento EXCLUSIVO_ALUNOS deve gerar comprovante digital completo")
    void eventoExclusivoDeveGerarComprovanteDigital() {
        ComprovanteResponse comprovante = comprovanteService.emitir(
                criarEvento(ModalidadeEvento.EXCLUSIVO_ALUNOS), participante, inscricao);

        assertInstanceOf(ComprovanteDigitalResponse.class, comprovante);
    }

    @Test
    @DisplayName("Evento RESTRICAO_IDADE deve gerar comprovante digital completo")
    void eventoComRestricaoDeveGerarComprovanteDigital() {
        ComprovanteResponse comprovante = comprovanteService.emitir(
                criarEvento(ModalidadeEvento.RESTRICAO_IDADE), participante, inscricao);

        assertInstanceOf(ComprovanteDigitalResponse.class, comprovante);
    }

    @Test
    @DisplayName("Deve falhar na criacao quando dois emissores atenderem a mesma modalidade")
    void deveFalharQuandoHouverEmissoresDuplicadosParaModalidade() {
        List<EmissorComprovanteStrategy> duplicados = List.of(
                new EmissorComprovanteSimplesStrategy(),
                new EmissorComprovanteSimplesStrategy()
        );

        assertThrows(IllegalStateException.class, () -> new ComprovanteService(duplicados));
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException quando nao houver emissor para a modalidade do evento")
    void deveFalharQuandoNaoHouverEmissorParaModalidade() {
        ComprovanteService somenteSimples = new ComprovanteService(List.of(new EmissorComprovanteSimplesStrategy()));
        Evento eventoExclusivo = criarEvento(ModalidadeEvento.EXCLUSIVO_ALUNOS);

        assertThrows(IllegalStateException.class, () -> somenteSimples.emitir(eventoExclusivo, participante, inscricao));
    }
}