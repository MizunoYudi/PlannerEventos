package com.example.plannereventos.service;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.EventoUpdateRequest;
import com.example.plannereventos.dto.EventoVagasResponse;
import com.example.plannereventos.exception.EventoCanceladoException;
import com.example.plannereventos.exception.EventoJaIniciadoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private InscricaoRepository inscricaoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento criarEventoPadrao(int id, String status, LocalDate data, LocalTime horaInicio) {
        return new Evento(
                id,
                "Workshop de Clean Code",
                "Boas práticas em Java",
                data,
                horaInicio,
                horaInicio.plusHours(2),
                "Auditório Principal",
                50,
                status,
                LocalDateTime.now()
        );
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
                "ATIVO",
                LocalDateTime.now()
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
    @DisplayName("Deve cancelar evento ativo futuro com sucesso")
    void deveCancelarEventoComSucesso() {
        Evento eventoAtivo = criarEventoPadrao(1, "ATIVO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoAtivo));
        when(eventoRepository.salvar(any(Evento.class))).thenReturn(eventoAtivo);

        EventoResponse response = eventoService.cancelar(1);

        assertNotNull(response);
        assertEquals("CANCELADO", response.getStatus());
        verify(eventoRepository, times(1)).salvar(eventoAtivo);
    }

    @Test
    @DisplayName("Deve lançar EventoCanceladoException ao tentar cancelar evento que já se encontra cancelado")
    void deveLancarExcecaoAoCancelarEventoJaCancelado() {
        Evento eventoJaCancelado = criarEventoPadrao(1, "CANCELADO", LocalDate.now().plusDays(5), LocalTime.of(15, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoJaCancelado));

        assertThrows(EventoCanceladoException.class, () -> {
            eventoService.cancelar(1);
        });

        verify(eventoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar EventoJaIniciadoException ao tentar cancelar evento no passado")
    void deveLancarExcecaoAoCancelarEventoJaIniciado() {
        Evento eventoPassado = criarEventoPadrao(1, "ATIVO", LocalDate.now().minusDays(1), LocalTime.of(10, 0));
        when(eventoRepository.buscarPorId(1)).thenReturn(Optional.of(eventoPassado));

        assertThrows(EventoJaIniciadoException.class, () -> {
            eventoService.cancelar(1);
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
}