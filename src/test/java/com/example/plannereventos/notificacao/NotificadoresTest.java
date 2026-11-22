package com.example.plannereventos.notificacao;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.StatusInscricao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificadoresTest {

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
    @DisplayName("NotificadorEmail deve enviar notificacao sem lancar excecao")
    void notificadorEmailDeveEnviarSemErro() {
        NotificadorEmail email = new NotificadorEmail();

        assertDoesNotThrow(() -> email.notificar(participante, evento, inscricao));
    }

    @Test
    @DisplayName("NotificadorEmail deve identificar o canal como E-MAIL")
    void notificadorEmailDeveInformarCanal() {
        assertEquals("E-MAIL", new NotificadorEmail().getCanal());
    }

    @Test
    @DisplayName("NotificadorSMS deve enviar notificacao sem lancar excecao quando a falha nao estiver simulada")
    void notificadorSmsDeveEnviarSemErro() {
        NotificadorSMS sms = new NotificadorSMS(false);

        assertDoesNotThrow(() -> sms.notificar(participante, evento, inscricao));
    }

    @Test
    @DisplayName("NotificadorSMS deve identificar o canal como SMS")
    void notificadorSmsDeveInformarCanal() {
        assertEquals("SMS", new NotificadorSMS(false).getCanal());
    }

    private List<String> capturarLogs(Class<?> classe, Runnable acao) {
        Logger logger = (Logger) LoggerFactory.getLogger(classe);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            acao.run();
            return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
        } finally {
            logger.detachAppender(appender);
        }
    }

    @Test
    @DisplayName("NotificadorEmail deve enviar o lembrete de prazo com o tempo restante")
    void notificadorEmailDeveEnviarLembreteDePrazo() {
        List<String> logs = capturarLogs(NotificadorEmail.class,
                () -> new NotificadorEmail().notificarPrazoConfirmacao(participante, evento, inscricao, "5h e 30min"));

        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("5h e 30min"));
        assertTrue(logs.get(0).contains("Semana de Tecnologia"));
        assertTrue(logs.get(0).contains("maria@email.com"));
        assertTrue(logs.get(0).contains("CONFIRMAR"));
    }

    @Test
    @DisplayName("NotificadorSMS deve enviar o lembrete de prazo com o tempo restante")
    void notificadorSmsDeveEnviarLembreteDePrazo() {
        List<String> logs = capturarLogs(NotificadorSMS.class,
                () -> new NotificadorSMS(false).notificarPrazoConfirmacao(participante, evento, inscricao, "45min"));

        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("45min"));
        assertTrue(logs.get(0).contains("Semana de Tecnologia"));
        assertTrue(logs.get(0).contains("Maria da Silva"));
    }

    @Test
    @DisplayName("NotificadorSMS deve lancar excecao no lembrete de prazo quando a falha estiver simulada")
    void notificadorSmsDeveFalharNoPrazoQuandoSimulado() {
        NotificadorSMS sms = new NotificadorSMS(true);

        assertThrows(IllegalStateException.class,
                () -> sms.notificarPrazoConfirmacao(participante, evento, inscricao, "5h"));
    }

    @Test
    @DisplayName("NotificadorEmail deve avisar que as vagas se esgotaram")
    void notificadorEmailDeveAvisarVagasEsgotadas() {
        List<String> logs = capturarLogs(NotificadorEmail.class,
                () -> new NotificadorEmail().notificarVagasEsgotadas(participante, evento, inscricao));

        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("esgotadas"));
        assertTrue(logs.get(0).contains("Semana de Tecnologia"));
    }

    @Test
    @DisplayName("NotificadorSMS deve avisar que as vagas se esgotaram")
    void notificadorSmsDeveAvisarVagasEsgotadas() {
        List<String> logs = capturarLogs(NotificadorSMS.class,
                () -> new NotificadorSMS(false).notificarVagasEsgotadas(participante, evento, inscricao));

        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("esgotadas"));
    }

    @Test
    @DisplayName("NotificadorSMS deve lancar excecao ao avisar vagas esgotadas quando a falha estiver simulada")
    void notificadorSmsDeveFalharEmVagasEsgotadasQuandoSimulado() {
        NotificadorSMS sms = new NotificadorSMS(true);

        assertThrows(IllegalStateException.class,
                () -> sms.notificarVagasEsgotadas(participante, evento, inscricao));
    }
}