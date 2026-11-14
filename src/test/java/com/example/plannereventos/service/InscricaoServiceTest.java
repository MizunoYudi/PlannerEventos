package com.example.plannereventos.service;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.EventoSemVagasException;
import com.example.plannereventos.exception.StatusInvalidoException;
import com.example.plannereventos.model.*;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.strategy.*;
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

    @Mock
    private ElegibilidadeStrategy elegibilidadeStrategy;

    @Mock
    private PoliticaCancelamentoStrategy politicaCancelamentoStrategy;

    @Mock
    private CalculoValorPadraoStrategy calculoPadraoStrategy;

    @Mock
    private CalculoValorEventoAbertoStrategy calculoEventoAbertoStrategy;

    @Mock
    private ComprovanteService comprovanteService;

    private InscricaoService inscricaoService;

    private Evento eventoPadrao;
    private Participante participante;
    private UUID participanteId;

    @BeforeEach
    void setUp() {
        when(elegibilidadeStrategy.getModalidade()).thenReturn(ModalidadeEvento.EXCLUSIVO_ALUNOS);
        when(politicaCancelamentoStrategy.getModalidade()).thenReturn(ModalidadeEvento.EXCLUSIVO_ALUNOS);

        inscricaoService = new InscricaoService(
                inscricaoRepository,
                eventoRepository,
                participanteRepository,
                List.of(elegibilidadeStrategy),
                List.of(politicaCancelamentoStrategy),
                calculoPadraoStrategy,
                calculoEventoAbertoStrategy,
                comprovanteService
        );

        participanteId = UUID.randomUUID();
        participante = new Participante();
        participante.setId(participanteId);
        participante.setNome("Bruna Serra");

        eventoPadrao = new Evento();
        eventoPadrao.setId(1);
        eventoPadrao.setTitulo("Workshop Spring Boot");
        eventoPadrao.setData(LocalDate.now().plusDays(5));
        eventoPadrao.setHoraInicio(LocalTime.of(19, 0));
        eventoPadrao.setCapacidadeMaxima(50);
        eventoPadrao.setStatus("ATIVO");
        eventoPadrao.setModalidade(ModalidadeEvento.EXCLUSIVO_ALUNOS);
        eventoPadrao.setValorBase(100.0);
        eventoPadrao.setLoteAtivo(LoteInscricao.PROMOCIONAL);
    }

    @Test
    @DisplayName("RF22 / RN08 / RN09 - Deve criar inscrição no status PENDENTE e calcular valor com desconto do lote ativo")
    void deveCriarInscricaoPendenteComValorCalculado() {
        InscricaoCreateRequest request = new InscricaoCreateRequest();
        request.setParticipanteId(participanteId);

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existeAtivaOuConfirmada(1, participanteId)).thenReturn(false);
        when(calculoPadraoStrategy.calcular(100.0, LoteInscricao.PROMOCIONAL)).thenReturn(80.0);
        when(inscricaoRepository.salvar(any(Inscricao.class))).thenAnswer(invocation -> {
            Inscricao i = invocation.getArgument(0);
            i.setId(10);
            return i;
        });

        InscricaoResponse response = inscricaoService.inscrever(1, request);

        assertNotNull(response);
        assertEquals("PENDENTE", response.getStatus());
        assertEquals(LoteInscricao.PROMOCIONAL, response.getLoteAtivo());
        assertEquals(80.0, response.getValorFinal());
        verify(calculoPadraoStrategy).calcular(100.0, LoteInscricao.PROMOCIONAL);
        verify(inscricaoRepository).salvar(any(Inscricao.class));
    }

    @Test
    @DisplayName("RN08 - Deve calcular valor final zero quando evento for da modalidade ABERTO")
    void deveCalcularValorZeroParaEventoAberto() {
        eventoPadrao.setModalidade(ModalidadeEvento.ABERTO);
        InscricaoCreateRequest request = new InscricaoCreateRequest();
        request.setParticipanteId(participanteId);

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existeAtivaOuConfirmada(1, participanteId)).thenReturn(false);
        when(calculoEventoAbertoStrategy.calcular(100.0, LoteInscricao.PROMOCIONAL)).thenReturn(0.0);
        when(inscricaoRepository.salvar(any(Inscricao.class))).thenAnswer(i -> i.getArgument(0));

        InscricaoResponse response = inscricaoService.inscrever(1, request);

        assertEquals(0.0, response.getValorFinal());
        assertEquals("PENDENTE", response.getStatus());
        verify(calculoEventoAbertoStrategy).calcular(100.0, LoteInscricao.PROMOCIONAL);
        verifyNoInteractions(calculoPadraoStrategy);
    }

    @Test
    @DisplayName("RF24 / RN09 - Deve confirmar inscrição PENDENTE quando houver vagas disponíveis")
    void deveConfirmarInscricaoPendenteComVagasDisponiveis() {
        Inscricao inscricaoPendente = new Inscricao(
                10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.PENDENTE, LoteInscricao.PROMOCIONAL, 80.0, null
        );

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.buscarPorEventoEParticipante(1, participanteId)).thenReturn(Optional.of(inscricaoPendente));
        when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(10);
        when(inscricaoRepository.salvar(any(Inscricao.class))).thenAnswer(i -> i.getArgument(0));
        when(comprovanteService.emitir(any(), any(), any())).thenReturn(mock(ComprovanteResponse.class));
        InscricaoResponse response = inscricaoService.confirmarInscricao(1, participanteId);

        assertEquals("CONFIRMADA", response.getStatus());
        verify(inscricaoRepository).salvar(inscricaoPendente);
        verify(comprovanteService).emitir(eventoPadrao, participante, inscricaoPendente);
    }

    @Test
    @DisplayName("RN09 / RN10 - Deve lançar EventoSemVagasException ao confirmar inscrição sem vagas")
    void deveLancarExcecaoAoConfirmarSemVagas() {
        eventoPadrao.setCapacidadeMaxima(10);
        Inscricao inscricaoPendente = new Inscricao(
                10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.PENDENTE, LoteInscricao.PROMOCIONAL, 80.0, null
        );

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.buscarPorEventoEParticipante(1, participanteId)).thenReturn(Optional.of(inscricaoPendente));
        when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(10);

        assertThrows(EventoSemVagasException.class, () -> inscricaoService.confirmarInscricao(1, participanteId));
        verify(inscricaoRepository, never()).salvar(any());
        verifyNoInteractions(comprovanteService);
    }

    @Test
    @DisplayName("RF24 / RN10 - Deve lançar StatusInvalidoException ao tentar confirmar inscrição já CONFIRMADA ou CANCELADA")
    void deveLancarStatusInvalidoExceptionAoAlterarEstadoFinal() {
        Inscricao inscricaoConfirmada = new Inscricao(
                10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.CONFIRMADA, LoteInscricao.PROMOCIONAL, 80.0, null
        );

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));
        when(inscricaoRepository.buscarPorEventoEParticipante(1, participanteId)).thenReturn(Optional.of(inscricaoConfirmada));
        when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(5);

        assertThrows(StatusInvalidoException.class, () -> inscricaoService.confirmarInscricao(1, participanteId));
        verify(inscricaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("RF24 / RN09 - Deve cancelar inscrição PENDENTE aplicando política de cancelamento da modalidade")
    void deveCancelarInscricaoPendente() {
        Inscricao inscricaoPendente = new Inscricao(
                10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.PENDENTE, LoteInscricao.PROMOCIONAL, 80.0, null
        );
        InscricaoCancelarRequest cancelRequest = new InscricaoCancelarRequest();
        cancelRequest.setMotivoCancelamento("Imprevisto pessoal");

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(1, participanteId)).thenReturn(Optional.of(inscricaoPendente));

        inscricaoService.cancelarInscricao(1, participanteId, cancelRequest);

        assertEquals(StatusInscricao.CANCELADA, inscricaoPendente.getStatus());
        assertEquals("Imprevisto pessoal", inscricaoPendente.getMotivoCancelamento());
        verify(politicaCancelamentoStrategy).validarCancelamento(inscricaoPendente, "Imprevisto pessoal");
        verify(inscricaoRepository).salvar(inscricaoPendente);
    }

    @Test
    @DisplayName("RF24 / RN10 - Deve lançar StatusInvalidoException ao tentar cancelar inscrição que não está PENDENTE")
    void deveLancarExcecaoAoCancelarInscricaoJaConfirmada() {
        Inscricao inscricaoConfirmada = new Inscricao(
                10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.CONFIRMADA, LoteInscricao.PROMOCIONAL, 80.0, null
        );

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPadrao));
        when(participanteRepository.existePorId(participanteId)).thenReturn(true);
        when(inscricaoRepository.buscarPorEventoEParticipante(1, participanteId)).thenReturn(Optional.of(inscricaoConfirmada));

        assertThrows(StatusInvalidoException.class, () -> inscricaoService.cancelarInscricao(1, participanteId, null));
        verify(politicaCancelamentoStrategy, never()).validarCancelamento(any(), any());
        verify(inscricaoRepository, never()).salvar(any());
    }
}