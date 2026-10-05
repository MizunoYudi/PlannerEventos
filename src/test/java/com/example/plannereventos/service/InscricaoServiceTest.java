package com.example.plannereventos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.CancelamentoForaDoPrazoException;
import com.example.plannereventos.exception.CancelamentoInscricaoInvalidoException;
import com.example.plannereventos.exception.DataNascimentoObrigatoriaException;
import com.example.plannereventos.exception.EventoCanceladoException;
import com.example.plannereventos.exception.EventoJaIniciadoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.exception.EventoSemVagasException;
import com.example.plannereventos.exception.IdadeNaoPermitidaException;
import com.example.plannereventos.exception.InscricaoDuplicadaException;
import com.example.plannereventos.exception.InscricaoNaoEncontradaException;
import com.example.plannereventos.exception.MatriculaInvalidaException;
import com.example.plannereventos.exception.MotivoCancelamentoObrigatorioException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.strategy.CancelamentoEventoAbertoStrategy;
import com.example.plannereventos.strategy.CancelamentoEventoExclusivoAlunosStrategy;
import com.example.plannereventos.strategy.CancelamentoEventoRestricaoIdadeStrategy;
import com.example.plannereventos.strategy.EventoAbertoStrategy;
import com.example.plannereventos.strategy.EventoExclusivoAlunosStrategy;
import com.example.plannereventos.strategy.EventoRestricaoIdadeStrategy;
import com.example.plannereventos.strategy.PoliticaCancelamentoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.plannereventos.strategy.CancelamentoEventoAbertoStrategy;
import com.example.plannereventos.strategy.CancelamentoEventoExclusivoAlunosStrategy;
import com.example.plannereventos.strategy.CancelamentoEventoRestricaoIdadeStrategy;
import com.example.plannereventos.strategy.PoliticaCancelamentoStrategy;
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

    var estrategiasElegibilidade = List.of(
            new EventoAbertoStrategy(inscricaoRepository),
            new EventoExclusivoAlunosStrategy(),
            new EventoRestricaoIdadeStrategy()
    );

    List<PoliticaCancelamentoStrategy> politicasCancelamento = List.of(
            new CancelamentoEventoAbertoStrategy(eventoRepository),
            new CancelamentoEventoExclusivoAlunosStrategy(eventoRepository),
            new CancelamentoEventoRestricaoIdadeStrategy()
    );

    inscricaoService = new InscricaoService(
            inscricaoRepository,
            eventoRepository,
            participanteRepository,
            estrategiasElegibilidade,
            politicasCancelamento
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
    @DisplayName("Deve recusar inscricao quando matricula for nula")
    void deveRecusarAlunoComMatriculaNula() {

        int eventoId = 1;
        UUID participanteId = UUID.randomUUID();

        Evento evento = criarEventoValido(
                eventoId,
                "ATIVO",
                LocalDate.now().plusDays(5),
                LocalTime.of(10, 0),
                50
        );

        evento.setModalidade(ModalidadeEvento.EXCLUSIVO_ALUNOS);

        Participante participante
                = criarParticipanteValido(participanteId);

        participante.setMatricula(null);
        participante.setMatriculaAtiva(true);

        InscricaoCreateRequest request
                = new InscricaoCreateRequest(participanteId);

        when(eventoRepository.buscarPorId(eventoId))
                .thenReturn(Optional.of(evento));

        when(participanteRepository.buscarPorId(participanteId))
                .thenReturn(Optional.of(participante));

        assertThrows(
                MatriculaInvalidaException.class,
                () -> inscricaoService.inscrever(eventoId, request)
        );

        verify(inscricaoRepository, never()).salvar(any());
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

        Evento evento = criarEventoValido(
                eventoId,
                "ATIVO",
                LocalDate.now().plusDays(5),
                LocalTime.of(10, 0),
                50
        );

        evento.setModalidade(ModalidadeEvento.ABERTO);

        Inscricao inscricao = new Inscricao(
                1,
                eventoId,
                participanteId,
                LocalDateTime.now(),
                "CONFIRMADA",
                null
        );

        InscricaoCancelarRequest request
                = new InscricaoCancelarRequest(
                        "Desistência pessoal"
                );

        when(eventoRepository.buscarPorId(eventoId))
                .thenReturn(Optional.of(evento));

        when(participanteRepository.existePorId(participanteId))
                .thenReturn(true);

        when(inscricaoRepository.buscarPorEventoEParticipante(
                eventoId,
                participanteId
        )).thenReturn(Optional.of(inscricao));

        inscricaoService.cancelarInscricao(
                eventoId,
                participanteId,
                request
        );

        assertEquals(
                "CANCELADA",
                inscricao.getStatus()
        );

        assertEquals(
                "Desistência pessoal",
                inscricao.getMotivoCancelamento()
        );

        verify(
                inscricaoRepository,
                times(1)
        ).salvar(inscricao);
    }

    @Test
@DisplayName("Deve lancar InscricaoNaoEncontradaException ao tentar cancelar inscricao inexistente")
void deveLancarExcecaoAoCancelarInscricaoInexistente() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().plusDays(5),
            LocalTime.of(10, 0),
            50
    );

    InscricaoCancelarRequest request =
            new InscricaoCancelarRequest("Motivo");

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.empty());

    assertThrows(
            InscricaoNaoEncontradaException.class,
            () -> inscricaoService.cancelarInscricao(
                    eventoId,
                    participanteId,
                    request
            )
    );

    verify(inscricaoRepository, never())
            .salvar(any());
}

    @Test
@DisplayName("Deve lancar CancelamentoInscricaoInvalidoException ao cancelar inscricao ja cancelada")
void deveLancarExcecaoAoCancelarInscricaoJaCancelada() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().plusDays(5),
            LocalTime.of(10, 0),
            50
    );

    Inscricao inscricaoCancelada = new Inscricao(
            1,
            eventoId,
            participanteId,
            LocalDateTime.now(),
            "CANCELADA",
            "Desistencia do participante"
    );

    InscricaoCancelarRequest request =
            new InscricaoCancelarRequest("Motivo");

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.of(inscricaoCancelada));

    assertThrows(
            CancelamentoInscricaoInvalidoException.class,
            () -> inscricaoService.cancelarInscricao(
                    eventoId,
                    participanteId,
                    request
            )
    );

    verify(inscricaoRepository, never())
            .salvar(any());
}@Test
@DisplayName("Nao deve cancelar evento aberto apos o inicio")
void naoDeveCancelarEventoAbertoAposInicio() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().minusDays(1),
            LocalTime.of(10, 0),
            50
    );

    evento.setModalidade(ModalidadeEvento.ABERTO);

    Inscricao inscricao = new Inscricao(
            1,
            eventoId,
            participanteId,
            LocalDateTime.now(),
            "CONFIRMADA",
            null
    );

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.of(inscricao));

    assertThrows(
            CancelamentoForaDoPrazoException.class,
            () -> inscricaoService.cancelarInscricao(
                    eventoId,
                    participanteId,
                    null
            )
    );

    assertEquals("CONFIRMADA", inscricao.getStatus());

    verify(inscricaoRepository, never())
            .salvar(any());
}
@Test
@DisplayName("Deve exigir motivo no cancelamento de evento com restricao de idade")
void deveExigirMotivoCancelamentoRestricaoIdade() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().plusDays(5),
            LocalTime.of(10, 0),
            50
    );

    evento.setModalidade(
            ModalidadeEvento.RESTRICAO_IDADE
    );

    Inscricao inscricao = new Inscricao(
            1,
            eventoId,
            participanteId,
            LocalDateTime.now(),
            "CONFIRMADA",
            null
    );

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.of(inscricao));

    assertThrows(
            MotivoCancelamentoObrigatorioException.class,
            () -> inscricaoService.cancelarInscricao(
                    eventoId,
                    participanteId,
                    null
            )
    );

    assertEquals("CONFIRMADA", inscricao.getStatus());

    verify(inscricaoRepository, never())
            .salvar(any());
}

@Test
@DisplayName("Nao deve cancelar evento exclusivo para alunos com menos de 24 horas")
void naoDeveCancelarEventoExclusivoDentroDas24Horas() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().plusDays(1),
            LocalTime.now(),
            50
    );

    evento.setModalidade(
            ModalidadeEvento.EXCLUSIVO_ALUNOS
    );

    Inscricao inscricao = new Inscricao(
            1,
            eventoId,
            participanteId,
            LocalDateTime.now(),
            "CONFIRMADA",
            null
    );

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.of(inscricao));

    assertThrows(
            CancelamentoForaDoPrazoException.class,
            () -> inscricaoService.cancelarInscricao(
                    eventoId,
                    participanteId,
                    null
            )
    );

    verify(inscricaoRepository, never())
            .salvar(any());
}
    @Test
@DisplayName("Deve cancelar inscricao de evento aberto antes do inicio")
void deveCancelarInscricaoComSucesso() {

    int eventoId = 1;
    UUID participanteId = UUID.randomUUID();

    Evento evento = criarEventoValido(
            eventoId,
            "ATIVO",
            LocalDate.now().plusDays(5),
            LocalTime.of(10, 0),
            50
    );

    evento.setModalidade(ModalidadeEvento.ABERTO);

    Inscricao inscricao = new Inscricao(
            1,
            eventoId,
            participanteId,
            LocalDateTime.now(),
            "CONFIRMADA",
            null
    );

    InscricaoCancelarRequest request =
            new InscricaoCancelarRequest("Desistencia pessoal");

    when(eventoRepository.buscarPorId(eventoId))
            .thenReturn(Optional.of(evento));

    when(participanteRepository.existePorId(participanteId))
            .thenReturn(true);

    when(inscricaoRepository.buscarPorEventoEParticipante(
            eventoId,
            participanteId
    )).thenReturn(Optional.of(inscricao));

    inscricaoService.cancelarInscricao(
            eventoId,
            participanteId,
            request
    );

    assertEquals("CANCELADA", inscricao.getStatus());

    assertEquals(
            "Desistencia pessoal",
            inscricao.getMotivoCancelamento()
    );

    verify(inscricaoRepository, times(1))
            .salvar(inscricao);
}
}
