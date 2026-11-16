package com.example.plannereventos.controller;

import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.service.InscricaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscricaoControllerTest {

    @Mock
    private InscricaoService inscricaoService;

    private InscricaoController controller;
    private UUID participanteId;

    @BeforeEach
    void setUp() {
        controller = new InscricaoController(inscricaoService);
        participanteId = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST /api/eventos/{eventoId}/inscricoes - Deve retornar 201 Created")
    void deveInscreverComSucesso() {
        InscricaoCreateRequest request = mock(InscricaoCreateRequest.class);
        InscricaoResponse esperado = mock(InscricaoResponse.class);

        when(inscricaoService.inscrever(1, request)).thenReturn(esperado);

        ResponseEntity<InscricaoResponse> response = controller.inscrever(1, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(esperado, response.getBody());
        verify(inscricaoService).inscrever(1, request);
    }

    @Test
    @DisplayName("GET /api/eventos/{eventoId}/inscricoes - Deve retornar 200 OK com lista")
    void deveListarPorEvento() {
        List<InscricaoResponse> lista = List.of(mock(InscricaoResponse.class));
        when(inscricaoService.listarPorEvento(1)).thenReturn(lista);

        ResponseEntity<List<InscricaoResponse>> response = controller.listarPorEvento(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
        verify(inscricaoService).listarPorEvento(1);
    }

    @Test
    @DisplayName("GET /api/eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 200 OK")
    void deveBuscarPorEventoEParticipante() {
        InscricaoResponse esperado = mock(InscricaoResponse.class);
        when(inscricaoService.buscarPorEventoEParticipante(1, participanteId)).thenReturn(esperado);

        ResponseEntity<InscricaoResponse> response = controller.buscarPorEventoEParticipante(1, participanteId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
        verify(inscricaoService).buscarPorEventoEParticipante(1, participanteId);
    }

    @Test
    @DisplayName("PATCH /api/eventos/{eventoId}/inscricoes/{participanteId}/confirmar - Deve retornar 200 OK")
    void deveConfirmarInscricao() {
        InscricaoResponse esperado = mock(InscricaoResponse.class);
        when(inscricaoService.confirmarInscricao(1, participanteId)).thenReturn(esperado);

        ResponseEntity<InscricaoResponse> response = controller.confirmarInscricao(1, participanteId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
        verify(inscricaoService).confirmarInscricao(1, participanteId);
    }

    @Test
    @DisplayName("PATCH /api/eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 204 No Content")
    void deveCancelarInscricao() {
        InscricaoCancelarRequest request = mock(InscricaoCancelarRequest.class);

        ResponseEntity<Void> response = controller.cancelarInscricao(1, participanteId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(inscricaoService).cancelarInscricao(1, participanteId, request);
    }
}