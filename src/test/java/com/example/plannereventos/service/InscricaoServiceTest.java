package com.example.plannereventos.service;

import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.*;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.strategy.EventoAbertoStrategy;
import com.example.plannereventos.strategy.EventoExclusivoAlunosStrategy;
import com.example.plannereventos.strategy.EventoRestricaoIdadeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private InscricaoService inscricaoService;

    @BeforeEach
    void setUp() {
        var estrategias = List.of(
                new EventoAbertoStrategy(inscricaoRepository),
                new EventoExclusivoAlunosStrategy(),
                new EventoRestricaoIdadeStrategy()
        );

        inscricaoService = new InscricaoService(
                inscricaoRepository,
                eventoRepository,
                participanteRepository,
                estrategias
        );
    }

    private Evento criarEventoValido(int id, String status, LocalDate data, LocalTime horaInicio, int capacidade) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setTitulo("Tech Summit 2026");
        evento.setDescricao("Conferência de Tecnologia");
        evento.setData(data);
        evento.setHoraInicio(horaInicio);
        evento.setHoraFim(horaInicio.plusHours(4));
        evento.setLocal("Auditório Principal");
        evento.setCapacidadeMaxima(capacidade);
        evento.setModalidade(ModalidadeEvento.ABERTO);
        evento.setStatus(status);
        evento.setCriadoEm(LocalDateTime.now());
        return evento;
    }

    private Participante criarParticipanteValido(UUID id) {
        Participante p = new Participante();
        p.setId(id);
        p.setNome("Carlos Silva");
        p.setEmail("carlos@email.com");
        p.setMatricula("123456");
        p.setMatriculaAtiva(true);
        p.setDataNascimento(LocalDate.now().minusYears(25));
        p.setCriadoEm(LocalDateTime.now());
        return p;
    }

    @Test
    @DisplayName("Deve realizar inscricao com sucesso quando dados e regras forem validos")
    void deveRealizarInscricaoComSucesso() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(10, 0), 50);
        Participante participante = criarParticipanteValido(participanteId);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
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
    @DisplayName("Deve lancar MatriculaInvalidaException ao tentar inscrever em evento EXCLUSIVO_ALUNOS com participante sem matricula ativa")
    void deveLancarExcecaoQuandoAlunoNaoForElegivel() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(10, 0), 50);
        evento.setModalidade(ModalidadeEvento.EXCLUSIVO_ALUNOS);

        Participante participanteInvalido = criarParticipanteValido(participanteId);
        participanteInvalido.setMatriculaAtiva(false);

        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participanteInvalido));

        assertThrows(MatriculaInvalidaException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar IdadeNaoPermitidaException ao tentar inscrever em evento RESTRICAO_IDADE com participante menor que a idade minima")
    void deveLancarExcecaoQuandoIdadeForInsuficiente() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(10, 0), 50);
        evento.setModalidade(ModalidadeEvento.RESTRICAO_IDADE);
        evento.setIdadeMinima(18);

        Participante participanteMenor = criarParticipanteValido(participanteId);
        participanteMenor.setDataNascimento(LocalDate.now().minusYears(16));

        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participanteMenor));

        assertThrows(IdadeNaoPermitidaException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
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
        when(participanteRepository.buscarPorId(participanteInexistente)).thenReturn(Optional.empty());

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
        Participante participante = criarParticipanteValido(participanteId);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoCancelado));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

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
        Participante participante = criarParticipanteValido(participanteId);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoPassado));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

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
        Participante participante = criarParticipanteValido(participanteId);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existeConfirmada(eventoId, participanteId)).thenReturn(true);

        assertThrows(InscricaoDuplicadaException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar EventoSemVagasException quando a capacidade maxima for atingida em evento ABERTO")
    void deveLancarExcecaoQuandoEventoEstiverLotado() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEventoValido(eventoId, "ATIVO", LocalDate.now().plusDays(3), LocalTime.of(10, 0), 5);
        evento.setModalidade(ModalidadeEvento.ABERTO);

        Participante participante = criarParticipanteValido(participanteId);
        InscricaoCreateRequest request = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existeConfirmada(eventoId, participanteId)).thenReturn(false);
        when(inscricaoRepository.contarConfirmadasPorEvento(eventoId)).thenReturn(5);

        assertThrows(EventoSemVagasException.class, () -> {
            inscricaoService.inscrever(eventoId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve cancelar inscricao confirmada com sucesso com motivo")
    void deveCancelarInscricaoComSucesso() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Inscricao inscricao = new Inscricao(1, eventoId, participanteId, LocalDateTime.now(), "CONFIRMADA", null);
        InscricaoCancelarRequest request = new InscricaoCancelarRequest("Desistência pessoal");

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.of(inscricao));

        inscricaoService.cancelarInscricao(eventoId, participanteId, request);

        assertEquals("CANCELADA", inscricao.getStatus());
        assertEquals("Desistência pessoal", inscricao.getMotivoCancelamento());
        verify(inscricaoRepository, times(1)).salvar(inscricao);
    }

    @Test
    @DisplayName("Deve lancar InscricaoNaoEncontradaException ao tentar cancelar inscricao inexistente")
    void deveLancarExcecaoAoCancelarInscricaoInexistente() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        InscricaoCancelarRequest request = new InscricaoCancelarRequest("Motivo");

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.empty());

        assertThrows(InscricaoNaoEncontradaException.class, () -> {
            inscricaoService.cancelarInscricao(eventoId, participanteId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar CancelamentoInscricaoInvalidoException ao cancelar inscricao ja cancelada")
    void deveLancarExcecaoAoCancelarInscricaoJaCancelada() {
        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();
        Inscricao inscricaoCancelada = new Inscricao(
                1,
                eventoId,
                participanteId,
                LocalDateTime.now(),
                "CANCELADA",
                "Desistência do participante"
        );
        InscricaoCancelarRequest request = new InscricaoCancelarRequest("Motivo");

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)).thenReturn(Optional.of(inscricaoCancelada));

        assertThrows(CancelamentoInscricaoInvalidoException.class, () -> {
            inscricaoService.cancelarInscricao(eventoId, participanteId, request);
        });

        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve listar inscricoes por evento com sucesso")
    void deveListarInscricoesPorEvento() {
        int eventoId = 1;
        List<Inscricao> lista = List.of(
                new Inscricao(1, eventoId, UUID.randomUUID(), LocalDateTime.now(), "CONFIRMADA", null),
                new Inscricao(2, eventoId, UUID.randomUUID(), LocalDateTime.now(), "CONFIRMADA", null)
        );

        when(eventoRepository.existePorId(eventoId)).thenReturn(true);
        when(inscricaoRepository.listarPorEvento(eventoId)).thenReturn(lista);

        List<InscricaoResponse> resultado = inscricaoService.listarPorEvento(eventoId);

        assertEquals(2, resultado.size());
        verify(inscricaoRepository, times(1)).listarPorEvento(eventoId);
    }
}