package com.example.plannereventos.service;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.notificacao.Notificador;
import com.example.plannereventos.notificacao.NotificadorSMS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private Notificador notificadorEmail;

    @Mock
    private Notificador notificadorSms;

    private Participante participante;
    private Evento evento;
    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        UUID participanteId = UUID.randomUUID();

        participante = new Participante();
        participante.setId(participanteId);
        participante.setNome("Maria da Silva");
        participante.setEmail("maria@email.com");

        evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Semana de Tecnologia");

        inscricao = new Inscricao(10, 1, participanteId, LocalDateTime.now(), "CONFIRMADA", null);
    }

    @Test
    @DisplayName("Deve disparar todos os notificadores cadastrados")
    void deveDispararTodosOsNotificadores() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail, notificadorSms));

        service.notificarMudancaDeStatus(participante, evento, inscricao);

        verify(notificadorEmail, times(1)).notificar(participante, evento, inscricao);
        verify(notificadorSms, times(1)).notificar(participante, evento, inscricao);
    }

    @Test
    @DisplayName("RN11: falha em um canal nao deve impedir o envio pelos demais nem propagar a excecao")
    void deveContinuarDisparandoQuandoUmNotificadorFalhar() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorSms, notificadorEmail));
        doThrow(new IllegalStateException("Servico de SMS indisponivel"))
                .when(notificadorSms).notificar(any(), any(), any());
        when(notificadorSms.getCanal()).thenReturn("SMS");

        assertDoesNotThrow(() -> service.notificarMudancaDeStatus(participante, evento, inscricao));

        InOrder ordem = inOrder(notificadorSms, notificadorEmail);
        ordem.verify(notificadorSms).notificar(participante, evento, inscricao);
        ordem.verify(notificadorEmail).notificar(participante, evento, inscricao);
    }

    @Test
    @DisplayName("RN11: falha em todos os canais nao deve propagar excecao")
    void naoDevePropagarExcecaoQuandoTodosOsNotificadoresFalharem() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail, notificadorSms));
        doThrow(new IllegalStateException("falha e-mail"))
                .when(notificadorEmail).notificar(any(), any(), any());
        doThrow(new IllegalStateException("falha SMS"))
                .when(notificadorSms).notificar(any(), any(), any());

        assertDoesNotThrow(() -> service.notificarMudancaDeStatus(participante, evento, inscricao));

        verify(notificadorEmail).notificar(participante, evento, inscricao);
        verify(notificadorSms).notificar(participante, evento, inscricao);
    }

    @Test
    @DisplayName("Deve funcionar sem nenhum notificador cadastrado")
    void naoDeveFalharSemNotificadores() {
        NotificacaoService service = new NotificacaoService(List.of());

        assertDoesNotThrow(() -> service.notificarMudancaDeStatus(participante, evento, inscricao));
    }

    @Test
    @DisplayName("NotificadorSMS deve lancar excecao quando a falha estiver simulada")
    void notificadorSmsDeveLancarExcecaoQuandoFalhaSimulada() {
        NotificadorSMS sms = new NotificadorSMS(true);

        assertThrows(IllegalStateException.class,
                () -> sms.notificar(participante, evento, inscricao));
    }

    @Test
    @DisplayName("Falha simulada no SMS real nao deve impedir o envio do e-mail")
    void falhaSimuladaNoSmsNaoDeveImpedirEmail() {
        NotificacaoService service = new NotificacaoService(List.of(new NotificadorSMS(true), notificadorEmail));

        assertDoesNotThrow(() -> service.notificarMudancaDeStatus(participante, evento, inscricao));

        verify(notificadorEmail, times(1)).notificar(participante, evento, inscricao);
    }
}