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

import java.time.LocalDate;
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
        ParticipanteCreateRequest request = new ParticipanteCreateRequest(
                "Maria Silva",
                "maria@email.com",
                "2023101",
                true,
                LocalDate.of(2000, 5, 15)
        );

        Participante participanteSalvo = new Participante();
        participanteSalvo.setId(UUID.randomUUID());
        participanteSalvo.setNome("Maria Silva");
        participanteSalvo.setEmail("maria@email.com");
        participanteSalvo.setMatricula("2023101");
        participanteSalvo.setMatriculaAtiva(true);
        participanteSalvo.setDataNascimento(LocalDate.of(2000, 5, 15));
        participanteSalvo.setCriadoEm(LocalDateTime.now());

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
        ParticipanteCreateRequest request = new ParticipanteCreateRequest(
                "João Silva",
                "joao@email.com",
                "2023102",
                true,
                LocalDate.of(1998, 10, 10)
        );
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
        Participante participante = new Participante();
        participante.setId(id);
        participante.setNome("Ana Souza");
        participante.setEmail("ana@email.com");
        participante.setCriadoEm(LocalDateTime.now());

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
        Participante participanteExistente = new Participante();
        participanteExistente.setId(id);
        participanteExistente.setNome("Lucas Lima");
        participanteExistente.setEmail("lucas@email.com");
        participanteExistente.setCriadoEm(LocalDateTime.now());

        ParticipanteUpdateRequest request = new ParticipanteUpdateRequest(
                "Lucas Silva",
                "lucas.silva@email.com",
                "2023103",
                true,
                LocalDate.of(1995, 3, 20)
        );

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
        Participante p1 = new Participante();
        p1.setId(UUID.randomUUID());
        p1.setNome("P1");
        p1.setEmail("p1@email.com");

        Participante p2 = new Participante();
        p2.setId(UUID.randomUUID());
        p2.setNome("P2");
        p2.setEmail("p2@email.com");

        when(participanteRepository.listar()).thenReturn(List.of(p1, p2));

        List<ParticipanteResponse> resultado = participanteService.listar();

        assertEquals(2, resultado.size());
        verify(participanteRepository, times(1)).listar();
    }
}