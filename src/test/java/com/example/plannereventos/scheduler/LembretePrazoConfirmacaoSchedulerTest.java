package com.example.plannereventos.scheduler;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.model.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LembretePrazoConfirmacaoSchedulerTest {

    private static final Duration CINCO_HORAS = Duration.ofHours(5);

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
        return new Inscricao(id, idEvento, participanteId, LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_2, 100.0, null);
    }

    // ===================== Lembrete do prazo =====================

    @Test
    @DisplayName("Deve notificar inscricao PENDENTE com prazo de confirmacao em aberto e marcar o lembrete")
    void deveNotificarPendenteDentroDoPrazo() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService, times(1)).notificarPrazoConfirmacao(participante, evento, inscricao, CINCO_HORAS);
        assertNotNull(inscricao.getUltimoLembrete());
        verify(inscricaoRepository).salvar(inscricao);
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
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteA)).thenReturn(Optional.of(pA));
        when(participanteRepository.buscarPorId(participanteB)).thenReturn(Optional.of(pB));

        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService).notificarPrazoConfirmacao(pA, evento, inscA, CINCO_HORAS);
        verify(notificacaoService).notificarPrazoConfirmacao(pB, evento, inscB, CINCO_HORAS);
    }

    @Test
    @DisplayName("Nao deve reenviar o lembrete antes do intervalo configurado")
    void naoDeveReenviarAntesDoIntervalo() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();
        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService, times(1)).notificarPrazoConfirmacao(participante, evento, inscricao, CINCO_HORAS);
    }

    @Test
    @DisplayName("Nao deve reenviar o lembrete para inscricao ja lembrada")
    void naoDeveReenviarLembreteJaEnviado() {
        Evento evento = criarEvento(1, "ATIVO");
        Inscricao inscricao = criarPendente(10, 1, UUID.randomUUID());
        inscricao.registrarLembrete(LocalDateTime.now(Fuso.BRASILIA).minusMinutes(5));

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(participanteRepository, notificacaoService);
    }

    @Test
    @DisplayName("Nao deve fazer nada quando nao houver inscricoes pendentes")
    void naoDeveFazerNadaSemPendentes() {
        when(inscricaoRepository.listarPendentes()).thenReturn(List.of());

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(eventoRepository, eventoService, participanteRepository, notificacaoService);
    }

    // ===================== Expiracao do prazo =====================

    @Test
    @DisplayName("Deve cancelar a inscricao e notificar o participante quando o prazo expirar")
    void deveExpirarInscricaoQuandoPrazoExpirou() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ofMinutes(-1));
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();

        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
        assertEquals("Prazo de confirmacao expirado", inscricao.getMotivoCancelamento());
        verify(inscricaoRepository).salvar(inscricao);
        verify(notificacaoService).notificarMudancaDeStatus(participante, evento, inscricao);
        verify(notificacaoService, never()).notificarPrazoConfirmacao(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve expirar a inscricao quando o tempo restante for exatamente zero")
    void deveExpirarInscricaoQuandoTempoRestanteForZero() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(Duration.ZERO);
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();

        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
        verify(notificacaoService).notificarMudancaDeStatus(participante, evento, inscricao);
        verify(notificacaoService, never()).notificarPrazoConfirmacao(any(), any(), any(), any());
    }

    // ===================== Casos em que nada deve acontecer =====================

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
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.empty());

        scheduler.notificarInscricoesPendentes();

        verifyNoInteractions(notificacaoService);
        assertNull(inscricao.getUltimoLembrete());
    }

    // ===================== Resiliencia =====================

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

        verify(notificacaoService).notificarPrazoConfirmacao(pB, evento2, inscB, Duration.ofHours(3));
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
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteA))
                .thenReturn(Optional.of(criarParticipante(participanteA)));
        when(participanteRepository.buscarPorId(participanteB))
                .thenReturn(Optional.of(criarParticipante(participanteB)));
        doThrow(new RuntimeException("falha no envio")).doNothing()
                .when(notificacaoService).notificarPrazoConfirmacao(any(), any(), any(), any());

        assertDoesNotThrow(() -> scheduler.notificarInscricoesPendentes());

        verify(notificacaoService, times(2)).notificarPrazoConfirmacao(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve reenviar o lembrete quando o intervalo configurado ja passou")
    void deveReenviarLembreteAposIntervalo() {
        UUID participanteId = UUID.randomUUID();
        Evento evento = criarEvento(1, "ATIVO");
        Participante participante = criarParticipante(participanteId);
        Inscricao inscricao = criarPendente(10, 1, participanteId);
        LocalDateTime lembreteAnterior = LocalDateTime.now(Fuso.BRASILIA).minusMinutes(20);
        inscricao.registrarLembrete(lembreteAnterior);

        when(inscricaoRepository.listarPendentes()).thenReturn(List.of(inscricao));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(eventoService.obterTempoRestante(evento)).thenReturn(CINCO_HORAS);
        when(participanteRepository.buscarPorId(participanteId)).thenReturn(Optional.of(participante));

        scheduler.notificarInscricoesPendentes();

        verify(notificacaoService).notificarPrazoConfirmacao(participante, evento, inscricao, CINCO_HORAS);
        assertTrue(inscricao.getUltimoLembrete().isAfter(lembreteAnterior));
        verify(inscricaoRepository).salvar(inscricao);
    }
}