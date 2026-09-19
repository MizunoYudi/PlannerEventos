package com.example.plannereventos.service;

import com.example.plannereventos.dto.ParticipanteCreateRequest;
import com.example.plannereventos.dto.ParticipanteResponse;
import com.example.plannereventos.dto.ParticipanteUpdateRequest;
import com.example.plannereventos.exception.EmailJaCadastradoException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.ParticipanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private ParticipanteService participanteService;

    @Test
    @DisplayName("Deve cadastrar participante com sucesso quando o e-mail não estiver em uso")
    void deveCadastrarParticipanteComSucesso() {
        ParticipanteCreateRequest request = new ParticipanteCreateRequest("Maria Silva", "maria@email.com");
        Participante participanteSalvo = new Participante(
                UUID.randomUUID(),
                "Maria Silva",
                "maria@email.com",
                LocalDateTime.now()
        );

        when(participanteRepository.existePorEmail("maria@email.com")).thenReturn(false);
        when(participanteRepository.salvar(any(Participante.class))).thenReturn(participanteSalvo);

        ParticipanteResponse resultado = participanteService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.getNome());
        assertEquals("maria@email.com", resultado.getEmail());
        verify(participanteRepository, times(1)).salvar(any(Participante.class));
    }

    @Test
    @DisplayName("Deve lançar EmailJaCadastradoException ao tentar cadastrar e-mail duplicado")
    void deveLancarExcecaoQuandoEmailJaCadastradoNoCadastro() {
        ParticipanteCreateRequest request = new ParticipanteCreateRequest("João Silva", "joao@email.com");
        when(participanteRepository.existePorEmail("joao@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> {
            participanteService.cadastrar(request);
        });

        verify(participanteRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve retornar participante quando o ID existir")
    void deveBuscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Participante participante = new Participante(id, "Ana Souza", "ana@email.com", LocalDateTime.now());
        when(participanteRepository.buscarPorId(id)).thenReturn(Optional.of(participante));

        ParticipanteResponse resultado = participanteService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Ana Souza", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar ParticipanteNaoEncontradoException quando o ID não existir na busca")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(participanteRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ParticipanteNaoEncontradoException.class, () -> {
            participanteService.buscarPorId(idInexistente);
        });
    }

    @Test
    @DisplayName("Deve atualizar os dados do participante com sucesso")
    void deveAtualizarParticipanteComSucesso() {
        UUID id = UUID.randomUUID();
        Participante participanteExistente = new Participante(id, "Lucas Lima", "lucas@email.com", LocalDateTime.now());
        ParticipanteUpdateRequest request = new ParticipanteUpdateRequest("Lucas Silva", "lucas.silva@email.com");

        when(participanteRepository.buscarPorId(id)).thenReturn(Optional.of(participanteExistente));
        when(participanteRepository.existePorEmail("lucas.silva@email.com")).thenReturn(false);

        ParticipanteResponse resultado = participanteService.atualizar(id, request);

        assertNotNull(resultado);
        assertEquals("Lucas Silva", resultado.getNome());
        assertEquals("lucas.silva@email.com", resultado.getEmail());
        verify(participanteRepository, times(1)).salvar(participanteExistente);
    }

    @Test
    @DisplayName("Deve listar todos os participantes cadastrados")
    void deveListarTodosOsParticipantes() {
        List<Participante> participantes = List.of(
                new Participante(UUID.randomUUID(), "P1", "p1@email.com", LocalDateTime.now()),
                new Participante(UUID.randomUUID(), "P2", "p2@email.com", LocalDateTime.now())
        );
        when(participanteRepository.listar()).thenReturn(participantes);

        List<ParticipanteResponse> resultado = participanteService.listar();

        assertEquals(2, resultado.size());
        verify(participanteRepository, times(1)).listar();
    }
}