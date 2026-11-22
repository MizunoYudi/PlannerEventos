package com.example.plannereventos.scheduler;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.StatusInscricao;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.service.EventoService;
import com.example.plannereventos.service.NotificacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LembretePrazoConfirmacaoSchedulerTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private LembretePrazoConfirmacaoScheduler scheduler;

    private Evento criarEvento(int id, String status) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setTitulo("Semana de Tecnologia " + id);
        evento.setStatus(status);
        return evento;
    }

    private Participante criarParticipante(UUID id) {
        Participante participante = new Participante();
        participante.setId(id);
        participante.setNome("Participante " + id);
        participante.setEmail("participante@email.com");
        return participante;
    }

    private Inscricao criarPendente(int id, int idEvento, UUID participanteId) {
        return new Inscricao(id, idEvento, participanteId, LocalDateTime.now(),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_2, 100.0, null);
    }

    @Test
    @DisplayName("Deve notificar inscricao PENDENTE com prazo de confirmacao em aberto")
    void deveNotificarPendenteDentroDoPrazo() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofHours(5));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService, times(1)).notificarPrazoConfirmacao(participante, evento, inscricao);
    }

    @Test
    @DisplayName("Deve notificar todas as inscricoes pendentes")
    void deveNotificarVariasPendentes() {
        UUID participanteA = UUID.randomUUID();
        UUID participanteB = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante pA = criarParticipante(participanteA);
        Participante pB = criarParticipante(participanteB);
        Inscricao inscA = criarPendente(10, 1, participanteA);
        Inscricao inscB = criarPendente(11, 1, participanteB);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscA, inscB));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofHours(5));
        when(participanteRepository.buscarPorId(participanteA)).thenReturn(Optional.of(pA));
        when(participanteRepository.buscarPorId(participanteB)).thenReturn(Optional.of(pB));

        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService).notificarPrazoConfirmacao(pA, evento, inscA);
        verify(notificacaoService).notificarPrazoConfirmacao(pB, evento, inscB);
    }

    @Test
    @DisplayName("Nao deve fazer nada quando nao houver inscricoes pendentes")
    void naoDeveFazerNadaSemPendentes() {
        when(inscricaoRepository.listarPendentes()).thenReturn(List.of());

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(eventoRepository, eventoService, participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve notificar quando o prazo de confirmacao ja expirou")
    void naoDeveNotificarQuandoPrazoExpirou() {
        Inscricao inscricao = criarPendente(10, 1, UUID.randomUUID());
        Evento evento = criarEvento(1, "ATIVO");

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofMinutes(-1));

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve notificar quando o tempo restante for exatamente zero")
    void naoDeveNotificarQuandoTempoRestanteForZero() {
        Inscricao inscricao = criarPendente(10, 1, UUID.randomUUID());
        Evento evento = criarEvento(1, "ATIVO");

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ZERO);

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve notificar quando o evento nao for encontrado")
    void naoDeveNotificarQuandoEventoNaoExiste() {
        Inscricao inscricao = criarPendente(10, 1, UUID.randomUUID());

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.empty());

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(eventoService, participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve notificar quando o evento estiver CANCELADO")
    void naoDeveNotificarQuandoEventoCancelado() {
        Inscricao inscricao = criarPendente(10, 1, UUID.randomUUID());

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(criarEvento(1, "CANCELADO")));

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(eventoService, participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve notificar quando o participante nao for encontrado")
    void naoDeveNotificarQuandoParticipanteNaoExiste() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofHours(5));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.empty());

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("Erro ao processar uma inscricao nao deve interromper as demais")
    void erroEmUmaInscricaoNaoDeveInterromperAsDemais() {
        UUID participanteA = UUID.randomUUID();
        UUID participanteB = UUID.randomUUID();
        Evento evento2 = criarEvento(2, "ATIVO");
        Participante pB = criarParticipante(participanteB);
        Inscricao inscA = criarPendente(10, 1, participanteA);
        Inscricao inscB = criarPendente(11, 2, participanteB);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscA, inscB));
        when(eventoRepository.buscarPorId(1)).thenThrow(new IllegalStateException("falha inesperada"));
        when(eventoRepository.buscarPorId(2)).thenReturn(Optional.of(evento2));
        when(eventoService.obterTempoRestante(evento2)).thenReturn(Duration.ofHours(3));
        when(participanteRepository.buscarPorId(participanteB)).thenReturn(Optional.of(pB));

        assertDoesNotThrow(() -> scheduler.notificarInscricoesPendentes());

        verify(notificacaoService).notificarPrazoConfirmacao(pB, evento2, inscB);
    }

    @Test
    @DisplayName("Falha no envio de uma notificacao nao deve interromper as demais")
    void falhaNoEnvioNaoDeveInterromperAsDemais() {
        UUID participanteA = UUID.randomUUID();
        UUID participanteB = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Inscricao inscA = criarPendente(10, 1, participanteA);
        Inscricao inscB = criarPendente(11, 1, participanteB);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscA, inscB));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofHours(5));
        when(participanteRepository.buscarPorId(participanteA))
                .thenReturn(Optional.of(criarParticipante(participanteA)));
        when(participanteRepository.buscarPorId(participanteB))
                .thenReturn(Optional.of(criarParticipante(participanteB)));
        doThrow(new RuntimeException("falha no envio")).doNothing()
                .when(notificacaoService).notificarPrazoConfirmacao(any(), any(), any());

        assertDoesNotThrow(() -> scheduler.notificarInscricoesPendentes());

        verify(notificacaoService, times(2)).notificarPrazoConfirmacao(any(), any(), any());
    }
}