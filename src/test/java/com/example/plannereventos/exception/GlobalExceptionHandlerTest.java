package com.example.plannereventos.exception;

import com.example.plannereventos.dto.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String CAMINHO = "/api/eventos/1/inscricoes";

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn(CAMINHO);
    }

    @Test
    @DisplayName("Regra de negocio violada deve retornar 422 com ErrorMessage")
    void regraDeNegocioDeveRetornar422() {
        MatriculaInvalidaException ex = new MatriculaInvalidaException();

        ResponseEntity<ErrorMessage> resposta = handler.handleRegraDeNegocio(ex, request);

        assertEquals(422, resposta.getStatusCode().value());
        ErrorMessage corpo = resposta.getBody();
        assertEquals(422, corpo.getStatus());
        assertEquals(List.of(ex.getMessage()), corpo.getMensagens());
        assertEquals(CAMINHO, corpo.getPath());
        assertNotNull(corpo.getTimestamp());
        assertNotNull(corpo.getError());
    }

    @Test
    @DisplayName("Recurso inexistente deve retornar 404 com a mensagem da excecao")
    void recursoNaoEncontradoDeveRetornar404() {
        EventoNaoEncontradoException ex = new EventoNaoEncontradoException(99);

        ResponseEntity<ErrorMessage> resposta = handler.handleRecursoNaoEncontrado(ex, request);

        assertEquals(404, resposta.getStatusCode().value());
        assertEquals(404, resposta.getBody().getStatus());
        assertEquals(List.of(ex.getMessage()), resposta.getBody().getMensagens());
        assertEquals(CAMINHO, resposta.getBody().getPath());
    }

    @Test
    @DisplayName("Falha de validacao deve retornar 400 com uma mensagem por campo")
    void validacaoDeveRetornar400ComUmaMensagemPorCampo() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "nome", "nao deve estar em branco"),
                new FieldError("request", "email", "e-mail invalido")
        ));

        ResponseEntity<ErrorMessage> resposta = handler.handleValidacao(ex, request);

        assertEquals(400, resposta.getStatusCode().value());
        assertEquals(
                List.of("nome: nao deve estar em branco", "email: e-mail invalido"),
                resposta.getBody().getMensagens());
        assertEquals(CAMINHO, resposta.getBody().getPath());
    }

    @Test
    @DisplayName("Corpo da requisicao ilegivel deve retornar 400")
    void corpoInvalidoDeveRetornar400() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorMessage> resposta = handler.handleCorpoInvalido(ex, request);

        assertEquals(400, resposta.getStatusCode().value());
        assertEquals(List.of("Corpo da requisicao invalido ou mal formatado"), resposta.getBody().getMensagens());
    }

    @Test
    @DisplayName("Parametro da URL com tipo errado deve retornar 400 citando o parametro")
    void parametroInvalidoDeveRetornar400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("participanteId");

        ResponseEntity<ErrorMessage> resposta = handler.handleParametroInvalido(ex, request);

        assertEquals(400, resposta.getStatusCode().value());
        assertEquals(List.of("Valor invalido para o parametro 'participanteId'"), resposta.getBody().getMensagens());
    }

    @Test
    @DisplayName("Erro interno deve retornar 500 sem expor o detalhe da excecao")
    void erroInternoDeveRetornar500SemVazarDetalhe() {
        IllegalStateException ex = new IllegalStateException("detalhe interno que nao pode vazar");

        ResponseEntity<ErrorMessage> resposta = handler.handleErroInterno(ex, request);

        assertEquals(500, resposta.getStatusCode().value());
        assertEquals(List.of("Erro interno do servidor"), resposta.getBody().getMensagens());
        assertFalse(resposta.getBody().getMensagens().toString().contains("detalhe interno"));
    }
}