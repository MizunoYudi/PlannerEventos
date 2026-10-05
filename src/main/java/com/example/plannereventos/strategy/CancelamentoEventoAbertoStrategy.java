package com.example.plannereventos.strategy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.plannereventos.exception.CancelamentoForaDoPrazoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.repository.EventoRepository;

@Component
public class CancelamentoEventoAbertoStrategy
        implements PoliticaCancelamentoStrategy {

    private final EventoRepository eventoRepository;

    public CancelamentoEventoAbertoStrategy(
            EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void validarCancelamento(
            Inscricao inscricao,
            String motivo) {

        Evento evento = eventoRepository
                .buscarPorId(inscricao.getIdEvento())
                .orElseThrow(()
                        -> new EventoNaoEncontradoException(
                        inscricao.getIdEvento()
                )
                );

        LocalDateTime inicioEvento = LocalDateTime.of(
                evento.getData(),
                evento.getHoraInicio()
        );

        if (!LocalDateTime.now().isBefore(inicioEvento)) {
            throw new CancelamentoForaDoPrazoException();
        }
    }

    @Override
    public ModalidadeEvento getModalidade() {
        return ModalidadeEvento.ABERTO;
    }
}
