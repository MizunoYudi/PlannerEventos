package com.example.plannereventos.strategy;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.exception.DataNascimentoObrigatoriaException;
import com.example.plannereventos.exception.EventoSemVagasException;
import com.example.plannereventos.exception.IdadeNaoPermitidaException;
import com.example.plannereventos.exception.MatriculaInvalidaException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.InscricaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElegibilidadeStrategyTest {

    private Evento evento;
    private Participante participante;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1);
        evento.setTitulo("Evento de Teste");
        evento.setData(LocalDate.now(Fuso.BRASILIA).plusDays(10));
        evento.setHoraInicio(LocalTime.of(10, 0));
        evento.setCapacidadeMaxima(2);

        participante = new Participante();
        participante.setId(UUID.randomUUID());
        participante.setNome("Ana Souza");
        participante.setMatricula("BV123456");
        participante.setMatriculaAtiva(true);
        participante.setDataNascimento(LocalDate.now(Fuso.BRASILIA).minusYears(25));
    }

    @Nested
    @DisplayName("RN01 - Evento ABERTO")
    class EventoAberto {

        @Mock
        private InscricaoRepository inscricaoRepository;

        @InjectMocks
        private EventoAbertoStrategy strategy;

        @Test
        @DisplayName("Deve permitir inscricao quando houver vagas")
        void devePermitirComVagas() {
            when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(1);

            assertDoesNotThrow(() -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar inscricao quando o evento estiver lotado")
        void deveRecusarEventoLotado() {
            when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(2);

            assertThrows(EventoSemVagasException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve atender a modalidade ABERTO")
        void deveAtenderModalidadeAberto() {
            assertEquals(ModalidadeEvento.ABERTO, strategy.getModalidade());
        }
    }

    @Nested
    @DisplayName("RN02 - Evento EXCLUSIVO_ALUNOS")
    class EventoExclusivoAlunos {

        private final EventoExclusivoAlunosStrategy strategy = new EventoExclusivoAlunosStrategy();

        @Test
        @DisplayName("Deve permitir aluno com matricula ativa")
        void devePermitirMatriculaAtiva() {
            assertDoesNotThrow(() -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar participante sem matricula")
        void deveRecusarSemMatricula() {
            participante.setMatricula(null);

            assertThrows(MatriculaInvalidaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar matricula em branco")
        void deveRecusarMatriculaEmBranco() {
            participante.setMatricula("   ");

            assertThrows(MatriculaInvalidaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar matricula inativa")
        void deveRecusarMatriculaInativa() {
            participante.setMatriculaAtiva(false);

            assertThrows(MatriculaInvalidaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar quando a situacao da matricula nao for informada")
        void deveRecusarMatriculaAtivaNula() {
            participante.setMatriculaAtiva(null);

            assertThrows(MatriculaInvalidaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve atender a modalidade EXCLUSIVO_ALUNOS")
        void deveAtenderModalidadeExclusivo() {
            assertEquals(ModalidadeEvento.EXCLUSIVO_ALUNOS, strategy.getModalidade());
        }
    }

    @Nested
    @DisplayName("RN03 - Evento RESTRICAO_IDADE")
    class EventoRestricaoIdade {

        private final EventoRestricaoIdadeStrategy strategy = new EventoRestricaoIdadeStrategy();

        @BeforeEach
        void configurarIdadeMinima() {
            evento.setIdadeMinima(18);
        }

        @Test
        @DisplayName("Deve permitir participante com idade acima da minima")
        void devePermitirIdadeSuficiente() {
            assertDoesNotThrow(() -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve permitir participante que completa a idade minima no dia do evento")
        void devePermitirIdadeExataNoDiaDoEvento() {
            participante.setDataNascimento(evento.getData().minusYears(18));

            assertDoesNotThrow(() -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar participante abaixo da idade minima")
        void deveRecusarIdadeInsuficiente() {
            participante.setDataNascimento(evento.getData().minusYears(17));

            assertThrows(IdadeNaoPermitidaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve recusar participante sem data de nascimento")
        void deveRecusarSemDataNascimento() {
            participante.setDataNascimento(null);

            assertThrows(DataNascimentoObrigatoriaException.class,
                    () -> strategy.validarElegibilidade(evento, participante));
        }

        @Test
        @DisplayName("Deve atender a modalidade RESTRICAO_IDADE")
        void deveAtenderModalidadeRestricao() {
            assertEquals(ModalidadeEvento.RESTRICAO_IDADE, strategy.getModalidade());
        }
    }
}