package com.example.plannereventos.exception;

import com.example.plannereventos.dto.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            EmailJaCadastradoException.class,
            InscricaoDuplicadaException.class,
            EventoSemVagasException.class,
            EventoCanceladoException.class,
            EventoJaIniciadoException.class,
            CancelamentoInscricaoInvalidoException.class,
            MatriculaInvalidaException.class,
            DataNascimentoObrigatoriaException.class,
            IdadeNaoPermitidaException.class,
            CancelamentoForaDoPrazoException.class,
            MotivoCancelamentoObrigatorioException.class
    })
    public ResponseEntity<ErrorMessage> handleRegraDeNegocio(Exception ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.UNPROCESSABLE_ENTITY, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler({
            ParticipanteNaoEncontradoException.class,
            EventoNaoEncontradoException.class,
            InscricaoNaoEncontradaException.class
    })
    public ResponseEntity<ErrorMessage> handleRecursoNaoEncontrado(Exception ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.NOT_FOUND, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return montarResposta(HttpStatus.BAD_REQUEST, erros, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleCorpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST,
                List.of("Corpo da requisicao invalido ou mal formatado"), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleParametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.BAD_REQUEST,
                List.of("Valor invalido para o parametro '" + ex.getName() + "'"), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleErroInterno(IllegalStateException ex, HttpServletRequest request) {
        log.error("Erro interno ao processar " + request.getRequestURI(), ex);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Erro interno do servidor"), request);
    }

    private ResponseEntity<ErrorMessage> montarResposta(HttpStatus status, List<String> mensagens, HttpServletRequest request) {
        ErrorMessage corpo = new ErrorMessage(
                status.value(),
                status.getReasonPhrase(),
                mensagens,
                request.getRequestURI());

        return ResponseEntity.status(status).body(corpo);
    }
}