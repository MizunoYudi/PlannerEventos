package com.example.plannereventos.service;

import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.*;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscricaoServiceTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private InscricaoService inscricaoService;

    private Evento criarEventoValido(int id, String status, LocalDate data, LocalTime horaInicio, int capacidade) {
        return new Evento(
                id,
                "Tech Summit 2026",
                "Conferência de Tecnologia",
                data,
                horaInicio,
                horaInicio.plusHours(4),
                "Auditório Principal",
                capacidade,
                status,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve realizar inscricao com sucesso quando dados e regras forem validos")
    void deveRealizarInscricaoComSucesso() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(10, 0), 50);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.existeConfirmada(eventoId, participanteId)).thenReturn(false);
        when(inscricaoRepository.contarConfirmadasPorEvento(eventoId)).thenReturn(10);
        when(inscricaoRepository.salvar(any(Inscricao.class))).thenAnswer(inv -> {
            Inscricao i = inv.getArgument(0);
            i.setId(100);
            return i;
        });

        InscricaoResponse response = inscricaoService.inscrever(eventoId, request);

        assertNotNull(response);
        assertEquals(eventoId, response.getEventoId());
        assertEquals(participanteId, response.getParticipanteId());
        assertEquals("CONFIRMADA", response.getStatus());
        verify(inscricaoRepository, times(1)).salvar(any(Inscricao.class));
    }

    @Test
    @DisplayName("Deve lancar EventoNaoEncontradoException quando evento nao existir")
    void deveLancarExcecaoQuandoEventoNaoEncontrarAoInscrever() {
        int eventoInexistente = 99;
        UUID participanteId = UUID.randomUUID();
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoInexistente)).thenReturn(Optional.empty());

        assertThrows(EventoNaoEncontradoException.class, () -> {
            inscricaoService.inscrever(eventoInexistente, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar ParticipanteNaoEncontradoException quando participante nao existir")
    void deveLancarExcecaoQuandoParticipanteNaoEncontrarAoInscrever() {
        int eventoId = 1;
        UUID participanteInexistente = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(2), LocalTime.of(9, 0), 20);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteInexistente);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.existePorId(participanteInexistente)).thenReturn(false);

        assertThrows(ParticipanteNaoEncontradoException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar EventoCanceladoException ao tentar inscrever em evento cancelado")
    void deveLancarExcecaoAoInscreverEmEventoCancelado() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento eventoCancelado = criarEventoValido(eventoId, "CANCELADO", LocalDate.now().plusDays(2), LocalTime.of(9, 0), 20);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoCancelado));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);

        assertThrows(EventoCanceladoException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar EventoJaIniciadoException ao tentar inscrever em evento passado")
    void deveLancarExcecaoAoInscreverEmEventoJaIniciado() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento eventoPassado = criarEventoValido(eventoId, "ATIVO", LocalDate.now().minusDays(1), LocalTime.of(9, 0), 20);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoPassado));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);

        assertThrows(EventoJaIniciadoException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar InscricaoDuplicadaException ao inscrever participante ja confirmado")
    void deveLancarExcecaoQuandoInscricaoForDuplicada() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(3), LocalTime.of(10, 0), 20);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.existeConfirmada(eventoId, participanteId)).thenReturn(true);

        assertThrows(InscricaoDuplicadaException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar EventoSemVagasException quando a capacidade maxima for atingida")
    void deveLancarExcecaoQuandoEventoEstiverLotado() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(3), LocalTime.of(10, 0), 5);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.existeConfirmada(eventoId, participanteId)).thenReturn(false);
        when(inscricaoRepository.contarConfirmadasPorEvento(eventoId)).thenReturn(5);

        assertThrows(EventoSemVagasException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve cancelar inscricao confirmada com sucesso")
    void deveCancelarInscricaoComSucesso() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Inscricao inscricao = new Inscricao(1, eventoId, participanteId, LocalDateTime.now(), "CONFIRMADA");

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.of(inscricao));

        inscricaoService.cancelarInscricao(eventoId, participanteId);

        assertEquals("CANCELADA", inscricao.getStatus());
        verify(inscricaoRepository, times(1)).salvar(inscricao);
    }

    @Test
    @DisplayName("Deve lancar InscricaoNaoEncontradaException ao tentar cancelar inscricao inexistente")
    void deveLancarExcecaoAoCancelarInscricaoInexistente() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.empty());

        assertThrows(InscricaoNaoEncontradaException.class, () -> {
            inscricaoService.cancelarInscricao(eventoId, participanteId);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar CancelamentoInscricaoInvalidoException ao cancelar inscricao ja cancelada")
    void deveLancarExcecaoAoCancelarInscricaoJaCancelada() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Inscricao inscricaoCancelada = new Inscricao(1, eventoId, participanteId, LocalDateTime.now(), "CANCELADA");

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.of(inscricaoCancelada));

        assertThrows(CancelamentoInscricaoInvalidoException.class, () -> {
            inscricaoService.cancelarInscricao(eventoId, participanteId);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve listar inscricoes por evento com sucesso")
    void deveListarInscricoesPorEvento() {
        int eventoId = 1;
        List<Inscricao> lista = List.of(
                new Inscricao(1, eventoId, UUID.randomUUID(), LocalDateTime.now(), "CONFIRMADA"),
                new Inscricao(2, eventoId, UUID.randomUUID(), LocalDateTime.now(), "CONFIRMADA")
        );

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(inscricaoRepository.listarPorEvento(eventoId)).thenReturn(lista);

        List<InscricaoResponse> resultado = inscricaoService.listarPorEvento(eventoId);

        assertEquals(2, resultado.size());
        verify(inscricaoRepository, times(1)).listarPorEvento(eventoId);
    }
}