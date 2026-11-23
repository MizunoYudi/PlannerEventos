package com.example.plannereventos.strategy;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.exception.CancelamentoForaDoPrazoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.exception.MotivoCancelamentoObrigatorioException;
import com.example.plannereventos.model.*;
import com.example.plannereventos.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoliticaCancelamentoStrategyTest {

    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        inscricao = new Inscricao(1, 1, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.CONFIRMADA, LoteInscricao.LOTE_2, 0.0, null);
    }

    private Evento eventoComInicioEm(LocalDateTime inicio) {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setData(inicio.toLocalDate());
        evento.setHoraInicio(inicio.toLocalTime());
        return evento;
    }

    @Nested
    @DisplayName("RN04 - Cancelamento em evento ABERTO")
    class CancelamentoAberto {

        @Mock
        private EventoRepository eventoRepository;

        @InjectMocks
        private CancelamentoEventoAbertoStrategy strategy;

        @Test
        @DisplayName("Deve permitir cancelamento antes do inicio do evento")
        void devePermitirAntesDoInicio() {
            when(eventoRepository.buscarPorId(1))
                    .thenReturn(Optional.of(eventoComInicioEm(LocalDateTime.now(Fuso.BRASILIA).plusHours(1))));

            assertDoesNotThrow(() -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve recusar cancelamento apos o inicio do evento")
        void deveRecusarAposInicio() {
            when(eventoRepository.buscarPorId(1))
                    .thenReturn(Optional.of(eventoComInicioEm(LocalDateTime.now(Fuso.BRASILIA).minusHours(1))));

            assertThrows(CancelamentoForaDoPrazoException.class,
                    () -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve lancar excecao quando o evento nao existir")
        void deveFalharComEventoInexistente() {
            when(eventoRepository.buscarPorId(1)).thenReturn(Optional.empty());

            assertThrows(EventoNaoEncontradoException.class,
                    () -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve atender a modalidade ABERTO")
        void deveAtenderModalidadeAberto() {
            assertEquals(ModalidadeEvento.ABERTO, strategy.getModalidade());
        }
    }

    @Nested
    @DisplayName("RN04 - Cancelamento em evento EXCLUSIVO_ALUNOS")
    class CancelamentoExclusivo {

        @Mock
        private EventoRepository eventoRepository;

        @InjectMocks
        private CancelamentoEventoExclusivoAlunosStrategy strategy;

        @Test
        @DisplayName("Deve permitir cancelamento com mais de 24 horas de antecedencia")
        void devePermitirComMaisDe24Horas() {
            when(eventoRepository.buscarPorId(1))
                    .thenReturn(Optional.of(eventoComInicioEm(LocalDateTime.now(Fuso.BRASILIA).plusHours(48))));

            assertDoesNotThrow(() -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve recusar cancelamento com menos de 24 horas de antecedencia")
        void deveRecusarComMenosDe24Horas() {
            when(eventoRepository.buscarPorId(1))
                    .thenReturn(Optional.of(eventoComInicioEm(LocalDateTime.now(Fuso.BRASILIA).plusHours(12))));

            assertThrows(CancelamentoForaDoPrazoException.class,
                    () -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve lancar excecao quando o evento nao existir")
        void deveFalharComEventoInexistente() {
            when(eventoRepository.buscarPorId(1)).thenReturn(Optional.empty());

            assertThrows(EventoNaoEncontradoException.class,
                    () -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve atender a modalidade EXCLUSIVO_ALUNOS")
        void deveAtenderModalidadeExclusivo() {
            assertEquals(ModalidadeEvento.EXCLUSIVO_ALUNOS, strategy.getModalidade());
        }
    }

    @Nested
    @DisplayName("RN04 - Cancelamento em evento RESTRICAO_IDADE")
    class CancelamentoRestricaoIdade {

        private final CancelamentoEventoRestricaoIdadeStrategy strategy =
                new CancelamentoEventoRestricaoIdadeStrategy();

        @Test
        @DisplayName("Deve permitir cancelamento com motivo informado")
        void devePermitirComMotivo() {
            assertDoesNotThrow(() -> strategy.validarCancelamento(inscricao, "Conflito de horario"));
        }

        @Test
        @DisplayName("Deve recusar cancelamento sem motivo")
        void deveRecusarSemMotivo() {
            assertThrows(MotivoCancelamentoObrigatorioException.class,
                    () -> strategy.validarCancelamento(inscricao, null));
        }

        @Test
        @DisplayName("Deve recusar cancelamento com motivo em branco")
        void deveRecusarMotivoEmBranco() {
            assertThrows(MotivoCancelamentoObrigatorioException.class,
                    () -> strategy.validarCancelamento(inscricao, "   "));
        }

        @Test
        @DisplayName("Deve atender a modalidade RESTRICAO_IDADE")
        void deveAtenderModalidadeRestricao() {
            assertEquals(ModalidadeEvento.RESTRICAO_IDADE, strategy.getModalidade());
        }
    }
}