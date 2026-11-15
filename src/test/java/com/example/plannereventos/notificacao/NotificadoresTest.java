package com.example.plannereventos.notificacao;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.StatusInscricao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
}