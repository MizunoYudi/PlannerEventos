package com.example.plannereventos.service;

import com.example.plannereventos.model.*;
import com.example.plannereventos.notificacao.Notificador;
import com.example.plannereventos.notificacao.NotificadorSMS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

        inscricao = new Inscricao(10, 1, participanteId, LocalDateTime.now(),
                StatusInscricao.CONFIRMADA, LoteInscricao.PROMOCIONAL, 80.0, null);
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

    private void definirInicioDoEvento(Duration ate) {
        LocalDateTime inicio = LocalDateTime.now().plus(ate);
        evento.setData(inicio.toLocalDate());
        evento.setHoraInicio(inicio.toLocalTime());
    }

    private String capturarTempoFormatado() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(notificadorEmail).notificarPrazoConfirmacao(
                eq(participante), eq(evento), eq(inscricao), captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("Prazo: deve disparar o lembrete em todos os notificadores cadastrados")
    void deveDispararPrazoEmTodosOsNotificadores() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail, notificadorSms));
        definirInicioDoEvento(Duration.ofHours(48));

        service.notificarPrazoConfirmacao(participante, evento, inscricao);

        verify(notificadorEmail, times(1))
                .notificarPrazoConfirmacao(eq(participante), eq(evento), eq(inscricao), anyString());
        verify(notificadorSms, times(1))
                .notificarPrazoConfirmacao(eq(participante), eq(evento), eq(inscricao), anyString());
    }

    @Test
    @DisplayName("Prazo: deve formatar o tempo restante em horas e minutos")
    void deveFormatarTempoRestanteEmHorasEMinutos() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail));
        definirInicioDoEvento(Duration.ofHours(24).plusMinutes(90));

        service.notificarPrazoConfirmacao(participante, evento, inscricao);

        assertTrue(capturarTempoFormatado().matches("1h e (29|30)min"));
    }

    @Test
    @DisplayName("Prazo: deve formatar apenas em minutos quando faltar menos de 1 hora")
    void deveFormatarTempoRestanteApenasEmMinutos() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail));
        definirInicioDoEvento(Duration.ofHours(24).plusMinutes(30));

        service.notificarPrazoConfirmacao(participante, evento, inscricao);

        assertTrue(capturarTempoFormatado().matches("(29|30)min"));
    }

    @Test
    @DisplayName("Prazo: deve informar no minimo 1min quando faltar menos de um minuto")
    void deveInformarNoMinimoUmMinuto() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail));
        definirInicioDoEvento(Duration.ofHours(24).plusSeconds(20));

        service.notificarPrazoConfirmacao(participante, evento, inscricao);

        assertEquals("1min", capturarTempoFormatado());
    }

    @Test
    @DisplayName("Prazo: deve respeitar o numero de horas configurado")
    void deveRespeitarPrazoConfigurado() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail));
        ReflectionTestUtils.setField(service, "prazoConfirmacaoHoras", 48L);
        definirInicioDoEvento(Duration.ofHours(48).plusMinutes(90));

        service.notificarPrazoConfirmacao(participante, evento, inscricao);

        assertTrue(capturarTempoFormatado().matches("1h e (29|30)min"));
    }

    @Test
    @DisplayName("RN11: falha em um canal nao deve impedir o lembrete de prazo nos demais")
    void deveContinuarLembreteDePrazoQuandoUmNotificadorFalhar() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorSms, notificadorEmail));
        definirInicioDoEvento(Duration.ofHours(48));
        doThrow(new IllegalStateException("Servico de SMS indisponivel"))
                .when(notificadorSms).notificarPrazoConfirmacao(any(), any(), any(), any());

        assertDoesNotThrow(() -> service.notificarPrazoConfirmacao(participante, evento, inscricao));

        InOrder ordem = inOrder(notificadorSms, notificadorEmail);
        ordem.verify(notificadorSms).notificarPrazoConfirmacao(eq(participante), eq(evento), eq(inscricao), anyString());
        ordem.verify(notificadorEmail).notificarPrazoConfirmacao(eq(participante), eq(evento), eq(inscricao), anyString());
    }

    @Test
    @DisplayName("RN11: falha em todos os canais no lembrete de prazo nao deve propagar excecao")
    void naoDevePropagarExcecaoNoPrazoQuandoTodosFalharem() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail, notificadorSms));
        definirInicioDoEvento(Duration.ofHours(48));
        doThrow(new IllegalStateException("falha e-mail"))
                .when(notificadorEmail).notificarPrazoConfirmacao(any(), any(), any(), any());
        doThrow(new IllegalStateException("falha SMS"))
                .when(notificadorSms).notificarPrazoConfirmacao(any(), any(), any(), any());

        assertDoesNotThrow(() -> service.notificarPrazoConfirmacao(participante, evento, inscricao));

        verify(notificadorEmail).notificarPrazoConfirmacao(any(), any(), any(), any());
        verify(notificadorSms).notificarPrazoConfirmacao(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Prazo: deve funcionar sem nenhum notificador cadastrado")
    void naoDeveFalharNoPrazoSemNotificadores() {
        NotificacaoService service = new NotificacaoService(List.of());
        definirInicioDoEvento(Duration.ofHours(48));

        assertDoesNotThrow(() -> service.notificarPrazoConfirmacao(participante, evento, inscricao));
    }

    @Test
    @DisplayName("Prazo: falha simulada no SMS real nao deve impedir o e-mail")
    void falhaSimuladaNoSmsNaoDeveImpedirEmailNoPrazo() {
        NotificacaoService service = new NotificacaoService(List.of(new NotificadorSMS(true), notificadorEmail));
        definirInicioDoEvento(Duration.ofHours(48));

        assertDoesNotThrow(() -> service.notificarPrazoConfirmacao(participante, evento, inscricao));

        verify(notificadorEmail, times(1))
                .notificarPrazoConfirmacao(eq(participante), eq(evento), eq(inscricao), anyString());
    }

    @Test
    @DisplayName("Vagas esgotadas: deve disparar todos os notificadores cadastrados")
    void deveDispararVagasEsgotadasEmTodosOsNotificadores() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorEmail, notificadorSms));

        service.notificarVagasEsgotadas(participante, evento, inscricao);

        verify(notificadorEmail, times(1)).notificarVagasEsgotadas(participante, evento, inscricao);
        verify(notificadorSms, times(1)).notificarVagasEsgotadas(participante, evento, inscricao);
    }

    @Test
    @DisplayName("Vagas esgotadas: falha em um canal nao deve impedir os demais nem propagar excecao")
    void deveContinuarVagasEsgotadasQuandoUmNotificadorFalhar() {
        NotificacaoService service = new NotificacaoService(List.of(notificadorSms, notificadorEmail));
        doThrow(new IllegalStateException("Servico de SMS indisponivel"))
                .when(notificadorSms).notificarVagasEsgotadas(any(), any(), any());

        assertDoesNotThrow(() -> service.notificarVagasEsgotadas(participante, evento, inscricao));

        verify(notificadorEmail).notificarVagasEsgotadas(participante, evento, inscricao);
    }
}