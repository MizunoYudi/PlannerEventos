package com.example.plannereventos.service;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.EventoUpdateRequest;
import com.example.plannereventos.dto.EventoVagasResponse;
import com.example.plannereventos.exception.EventoCanceladoException;
import com.example.plannereventos.exception.EventoJaIniciadoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.model.*;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private EventoService eventoService;

    private Evento criarEventoPadrao(int id, String status, LocalDate data, LocalTime horaInicio) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setTitulo("Workshop de Clean Code");
        evento.setDescricao("Boas práticas em Java");
        evento.setData(data);
        evento.setHoraInicio(horaInicio);
        evento.setHoraFim(horaInicio.plusHours(2));
        evento.setLocal("Auditório Principal");
        evento.setCapacidadeMaxima(50);
        evento.setModalidade(ModalidadeEvento.ABERTO);
        evento.setStatus(status);
        evento.setCriadoEm(LocalDateTime.now());
        return evento;
    }

    @Test
    @DisplayName("Deve cadastrar evento com sucesso com status ATIVO")
    void deveCadastrarEventoComSucesso() {
        EventoCreateRequest request = new EventoCreateRequest(
                "Workshop de Clean Code",
                "Boas práticas em Java",
                LocalDate.now().plusDays(5),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Auditório Principal",
                50,
                ModalidadeEvento.ABERTO,
                null
        );

        when(eventoRepository.salvar(any(Evento.class))).thenAnswer(invocation -> {
            Evento e = invocation.getArgument(0);
            e.setId(1);
            return e;
        });

        EventoResponse response = eventoService.cadastrar(request);

        assertNotNull(response);
        assertEquals("Workshop de Clean Code", response.getTitulo());
        assertEquals("ATIVO", response.getStatus());
        verify(eventoRepository, times(1)).salvar(any(Evento.class));
    }

    @Test
    @DisplayName("Deve buscar evento por ID com sucesso")
    void deveBuscarEventoPorIdComSucesso() {
        Evento evento = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));

        EventoResponse response = eventoService.buscarPorId(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Workshop de Clean Code", response.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar EventoNaoEncontradoException quando ID não existir")
    void deveLancarExcecaoQuandoEventoNaoEncontrado() {
        when(eventoRepository.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(EventoNaoEncontradoException.class, () -> {
            eventoService.buscarPorId(99);
        });
    }

    @Test
    @DisplayName("Deve atualizar evento ativo com sucesso")
    void deveAtualizarEventoComSucesso() {
        Evento eventoExistente = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        EventoUpdateRequest updateRequest = new EventoUpdateRequest();
        updateRequest.setTitulo("Título Atualizado");
        updateRequest.setDescricao("Nova Descrição");
        updateRequest.setData(LocalDate.now().plusDays(3));
        updateRequest.setHoraInicio(LocalTime.of(11, 0));
        updateRequest.setHoraFim(LocalTime.of(13, 0));
        updateRequest.setLocal("Sala B");
        updateRequest.setCapacidadeMaxima(60);
        updateRequest.setModalidade(ModalidadeEvento.ABERTO);

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoExistente));
        when(eventoRepository.salvar(any(Evento.class))).thenReturn(eventoExistente);

        EventoResponse response = eventoService.atualizar(1, updateRequest);

        assertNotNull(response);
        assertEquals("Título Atualizado", response.getTitulo());
        assertEquals(60, response.getCapacidadeMaxima());
        verify(eventoRepository, times(1)).salvar(eventoExistente);
    }

    @Test
    @DisplayName("Deve lançar EventoCanceladoException ao tentar atualizar evento já cancelado")
    void deveLancarExcecaoAoAtualizarEventoCancelado() {
        Evento eventoCancelado = criarEventoPadrao(1, "CANCELADO", LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        EventoUpdateRequest updateRequest = new EventoUpdateRequest();

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoCancelado));

        assertThrows(EventoCanceladoException.class, () -> {
            eventoService.atualizar(1, updateRequest);
        });

        verify(eventoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve cancelar evento ativo futuro com sucesso e cancelar as inscricoes")
    void deveCancelarEventoComSucesso() {
        Evento eventoAtivo = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));
        String motivo = "Problemas técnicos no local";

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoAtivo));
        when(eventoRepository.salvar(any(Evento.class))).thenReturn(eventoAtivo);

        EventoResponse response = eventoService.cancelar(1, motivo);

        assertNotNull(response);
        assertEquals("CANCELADO", response.getStatus());
        verify(eventoRepository, times(1)).salvar(eventoAtivo);
        verify(inscricaoRepository, times(1)).cancelarTodasPorEvento(eq(1), eq(motivo));
    }

    @Test
    @DisplayName("Deve lançar EventoCanceladoException ao tentar cancelar evento que já se encontra cancelado")
    void deveLancarExcecaoAoCancelarEventoJaCancelado() {
        Evento eventoJaCancelado = criarEventoPadrao(1, "CANCELADO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoJaCancelado));

        assertThrows(EventoCanceladoException.class, () -> {
            eventoService.cancelar(1, "Motivo");
        });

        verify(eventoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar EventoJaIniciadoException ao tentar cancelar evento no passado")
    void deveLancarExcecaoAoCancelarEventoJaIniciado() {
        Evento eventoPassado = criarEventoPadrao(1, "ATIVO", LocalDate.now().minusDays(1), LocalTime.of(10, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPassado));

        assertThrows(EventoJaIniciadoException.class, () -> {
            eventoService.cancelar(1, "Motivo");
        });

        verify(eventoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve calcular corretamente as vagas disponíveis")
    void deveConsultarVagasComSucesso() {
        Evento evento = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(3), LocalTime.of(10, 0));
        evento.setCapacidadeMaxima(100);

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.contarConfirmadasPorEvento(1)).thenReturn(35);

        EventoVagasResponse response = eventoService.consultarVagas(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(100, response.getCapacidadeMaxima());
        assertEquals(35, response.getInscricoesConfirmadas());
        assertEquals(65, response.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Deve listar todos os eventos cadastrados")
    void deveListarEventosComSucesso() {
        List<Evento> lista = List.of(
                criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(1), LocalTime.of(9, 0)),
                criarEventoPadrao(2, "ATIVO", LocalDate.now().plusDays(2), LocalTime.of(14, 0))
        );
        when(eventoRepository.listar()).thenReturn(lista);

        List<EventoResponse> resultado = eventoService.listar();

        assertEquals(2, resultado.size());
        verify(eventoRepository, times(1)).listar();
    }

    @Test
    @DisplayName("Deve cadastrar evento exclusivo para alunos")
    void deveCadastrarEventoExclusivoAlunos() {

        EventoCreateRequest request = new EventoCreateRequest(
                "Evento para alunos",
                "Evento exclusivo",
                LocalDate.now().plusDays(5),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Auditorio",
                50,
                ModalidadeEvento.EXCLUSIVO_ALUNOS,
                null
        );

        when(eventoRepository.salvar(any(Evento.class)))
                .thenAnswer(invocation -> {
                    Evento evento = invocation.getArgument(0);
                    evento.setId(1);
                    return evento;
                });

        EventoResponse response
                = eventoService.cadastrar(request);

        assertEquals(
                ModalidadeEvento.EXCLUSIVO_ALUNOS,
                response.getModalidade()
        );
    }

    @Test
    @DisplayName("Deve cadastrar evento com restricao de idade")
    void deveCadastrarEventoRestricaoIdade() {

        EventoCreateRequest request = new EventoCreateRequest(
                "Evento 18+",
                "Evento restrito",
                LocalDate.now().plusDays(5),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Auditorio",
                50,
                ModalidadeEvento.RESTRICAO_IDADE,
                18
        );

        when(eventoRepository.salvar(any(Evento.class)))
                .thenAnswer(invocation -> {
                    Evento evento = invocation.getArgument(0);
                    evento.setId(1);
                    return evento;
                });

        EventoResponse response
                = eventoService.cadastrar(request);

        assertEquals(
                ModalidadeEvento.RESTRICAO_IDADE,
                response.getModalidade()
        );

        assertEquals(
                18,
                response.getIdadeMinima()
        );
    }

    @Test
    @DisplayName("RF22 - Deve cadastrar evento com valor base e lote ativo informados")
    void deveCadastrarEventoComValorBaseELote() {
        EventoCreateRequest request = new EventoCreateRequest(
                "Workshop Pago", "Evento com inscricao paga",
                LocalDate.now().plusDays(5), LocalTime.of(14, 0), LocalTime.of(16, 0),
                "Auditório", 50, ModalidadeEvento.EXCLUSIVO_ALUNOS, null
        );
        request.setValorBase(100.0);
        request.setLoteAtivo(LoteInscricao.PROMOCIONAL);

        when(eventoRepository.salvar(any(Evento.class))).thenAnswer(i -> i.getArgument(0));

        EventoResponse response = eventoService.cadastrar(request);

        assertEquals(100.0, response.getValorBase());
        assertEquals(LoteInscricao.PROMOCIONAL, response.getLoteAtivo());
    }

    @Test
    @DisplayName("RF22 - Deve usar LOTE_2 quando o lote nao for informado")
    void deveUsarLote2QuandoLoteNaoInformado() {
        EventoCreateRequest request = new EventoCreateRequest(
                "Workshop", "Descricao",
                LocalDate.now().plusDays(5), LocalTime.of(14, 0), LocalTime.of(16, 0),
                "Auditório", 50, ModalidadeEvento.ABERTO, null
        );

        when(eventoRepository.salvar(any(Evento.class))).thenAnswer(i -> i.getArgument(0));

        EventoResponse response = eventoService.cadastrar(request);

        assertEquals(LoteInscricao.LOTE_2, response.getLoteAtivo());
    }

    @Test
    @DisplayName("RF25 - Deve notificar os participantes quando o evento for cancelado")
    void deveNotificarParticipantesAoCancelarEvento() {
        Evento evento = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));

        UUID idAna = UUID.randomUUID();
        UUID idBruno = UUID.randomUUID();
        Participante ana = new Participante();
        ana.setId(idAna);
        Participante bruno = new Participante();
        bruno.setId(idBruno);

        Inscricao inscricaoAna = new Inscricao(1, 1, idAna, LocalDateTime.now(),
                StatusInscricao.CANCELADA, LoteInscricao.LOTE_2, 0.0, "Evento cancelado");
        Inscricao inscricaoBruno = new Inscricao(2, 1, idBruno, LocalDateTime.now(),
                StatusInscricao.CANCELADA, LoteInscricao.LOTE_2, 0.0, "Evento cancelado");

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.cancelarTodasPorEvento(1, "Evento cancelado"))
                .thenReturn(List.of(inscricaoAna, inscricaoBruno));
        when(participanteRepository.buscarPorId(idAna)).thenReturn(Optional.of(ana));
        when(participanteRepository.buscarPorId(idBruno)).thenReturn(Optional.of(bruno));

        eventoService.cancelar(1, "Evento cancelado");

        verify(notificacaoService).notificarMudancaDeStatus(ana, evento, inscricaoAna);
        verify(notificacaoService).notificarMudancaDeStatus(bruno, evento, inscricaoBruno);
    }

    @Test
    @DisplayName("RF25 - Nao deve falhar o cancelamento do evento quando um participante nao for encontrado")
    void deveCancelarEventoMesmoSemEncontrarParticipante() {
        Evento evento = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));
        UUID idInexistente = UUID.randomUUID();
        Inscricao inscricao = new Inscricao(1, 1, idInexistente, LocalDateTime.now(),
                StatusInscricao.CANCELADA, LoteInscricao.LOTE_2, 0.0, "Evento cancelado");

        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.cancelarTodasPorEvento(1, "Evento cancelado")).thenReturn(List.of(inscricao));
        when(participanteRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        EventoResponse response = assertDoesNotThrow(() -> eventoService.cancelar(1, "Evento cancelado"));

        assertEquals("CANCELADO", response.getStatus());
        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("Deve calcular o tempo restante ate o limite de confirmacao (inicio - 24h)")
    void deveCalcularTempoRestanteParaConfirmacao() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(48);
        Evento evento = criarEventoPadrao(1, "ATIVO", inicio.toLocalDate(), inicio.toLocalTime());

        Duration restante = eventoService.obterTempoRestante(evento);

        assertTrue(restante.compareTo(Duration.ofHours(24)) <= 0);
        assertTrue(restante.compareTo(Duration.ofHours(23).plusMinutes(59)) > 0);
    }

    @Test
    @DisplayName("Deve retornar tempo restante negativo quando o limite de confirmacao ja passou")
    void deveRetornarTempoRestanteNegativoAposOLimite() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(10);
        Evento evento = criarEventoPadrao(1, "ATIVO", inicio.toLocalDate(), inicio.toLocalTime());

        Duration restante = eventoService.obterTempoRestante(evento);

        assertTrue(restante.isNegative());
    }

    @Test
    @DisplayName("Deve usar o numero de horas configurado para calcular o tempo restante")
    void deveUsarPrazoConfiguradoNoTempoRestante() {
        ReflectionTestUtils.setField(eventoService, "prazoConfirmacaoHoras", 48);
        LocalDateTime inicio = LocalDateTime.now().plusHours(72);
        Evento evento = criarEventoPadrao(1, "ATIVO", inicio.toLocalDate(), inicio.toLocalTime());

        Duration restante = eventoService.obterTempoRestante(evento);

        assertTrue(restante.compareTo(Duration.ofHours(24)) <= 0);
        assertTrue(restante.compareTo(Duration.ofHours(23).plusMinutes(59)) > 0);
    }
}
