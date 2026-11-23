package com.example.plannereventos.service;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.model.*;
import com.example.plannereventos.notificacao.Notificador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private Notificador email;

    @Mock
    private Notificador sms;

    private NotificacaoService notificacaoService;

    private Participante participante;
    private Evento evento;
    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(List.of(email, sms));

        UUID participanteId = UUID.randomUUID();

        participante = new Participante();
        participante.setId(participanteId);
        participante.setNome("Maria Teste");
        participante.setEmail("maria@email.com");

        evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Semana de Tecnologia");

        inscricao = new Inscricao(10, 1, participanteId, LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_2, 100.0, null);
    }

    @Nested
    @DisplayName("notificarMudancaDeStatus")
    class MudancaDeStatus {

        @Test
        @DisplayName("Deve notificar por todos os canais")
        void deveNotificarPorTodosOsCanais() {
            notificacaoService.notificarMudancaDeStatus(participante, evento, inscricao);

            verify(email).notificar(participante, evento, inscricao);
            verify(sms).notificar(participante, evento, inscricao);
        }

        @Test
        @DisplayName("Falha em um canal nao deve impedir o envio pelos demais")
        void falhaEmUmCanalNaoDeveImpedirOsDemais() {
            doThrow(new IllegalStateException("SMS indisponivel"))
                    .when(sms).notificar(participante, evento, inscricao);

            assertDoesNotThrow(() -> notificacaoService.notificarMudancaDeStatus(participante, evento, inscricao));

            verify(email).notificar(participante, evento, inscricao);
            verify(sms).notificar(participante, evento, inscricao);
        }

        @Test
        @DisplayName("Falha no primeiro canal nao deve impedir o envio pelo segundo")
        void falhaNoPrimeiroCanalNaoDeveImpedirOSegundo() {
            doThrow(new IllegalStateException("E-mail indisponivel"))
                    .when(email).notificar(participante, evento, inscricao);

            assertDoesNotThrow(() -> notificacaoService.notificarMudancaDeStatus(participante, evento, inscricao));

            verify(sms).notificar(participante, evento, inscricao);
        }
    }

    @Nested
    @DisplayName("notificarPrazoConfirmacao")
    class PrazoConfirmacao {

        @Test
        @DisplayName("Deve enviar o lembrete por todos os canais com horas exatas")
        void deveEnviarLembreteComHorasExatas() {
            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, Duration.ofHours(5));

            verify(email).notificarPrazoConfirmacao(participante, evento, inscricao, "5h");
            verify(sms).notificarPrazoConfirmacao(participante, evento, inscricao, "5h");
        }

        @Test
        @DisplayName("Deve formatar horas e minutos")
        void deveFormatarHorasEMinutos() {
            Duration tempo = Duration.ofHours(2).plusMinutes(30);

            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, tempo);

            verify(email).notificarPrazoConfirmacao(participante, evento, inscricao, "2h e 30min");
        }

        @Test
        @DisplayName("Deve formatar apenas minutos quando faltar menos de 1 hora")
        void deveFormatarApenasMinutos() {
            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, Duration.ofMinutes(45));

            verify(email).notificarPrazoConfirmacao(participante, evento, inscricao, "45min");
        }

        @Test
        @DisplayName("Deve informar 'menos de 1min' quando faltarem apenas segundos")
        void deveInformarMenosDeUmMinuto() {
            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, Duration.ofSeconds(30));

            verify(email).notificarPrazoConfirmacao(participante, evento, inscricao, "menos de 1min");
        }

        @Test
        @DisplayName("Nao deve enviar lembrete quando o prazo ja expirou")
        void naoDeveEnviarQuandoPrazoExpirou() {
            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, Duration.ofMinutes(-10));

            verifyNoInteractions(email, sms);
        }

        @Test
        @DisplayName("Nao deve enviar lembrete quando o tempo restante for zero")
        void naoDeveEnviarQuandoTempoForZero() {
            notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, Duration.ZERO);

            verifyNoInteractions(email, sms);
        }

        @Test
        @DisplayName("Falha em um canal nao deve impedir o lembrete pelos demais")
        void falhaEmUmCanalNaoDeveImpedirLembrete() {
            doThrow(new IllegalStateException("SMS indisponivel"))
                    .when(sms).notificarPrazoConfirmacao(any(), any(), any(), any());

            assertDoesNotThrow(() -> notificacaoService.notificarPrazoConfirmacao(
                    participante, evento, inscricao, Duration.ofHours(3)));

            verify(email).notificarPrazoConfirmacao(participante, evento, inscricao, "3h");
        }
    }

    @Nested
    @DisplayName("notificarVagasEsgotadas")
    class VagasEsgotadas {

        @Test
        @DisplayName("Deve avisar por todos os canais")
        void deveAvisarPorTodosOsCanais() {
            notificacaoService.notificarVagasEsgotadas(participante, evento, inscricao);

            verify(email).notificarVagasEsgotadas(participante, evento, inscricao);
            verify(sms).notificarVagasEsgotadas(participante, evento, inscricao);
        }

        @Test
        @DisplayName("Falha em um canal nao deve impedir o aviso pelos demais")
        void falhaEmUmCanalNaoDeveImpedirAviso() {
            doThrow(new IllegalStateException("E-mail indisponivel"))
                    .when(email).notificarVagasEsgotadas(participante, evento, inscricao);

            assertDoesNotThrow(() -> notificacaoService.notificarVagasEsgotadas(participante, evento, inscricao));

            verify(sms).notificarVagasEsgotadas(participante, evento, inscricao);
        }
    }
}