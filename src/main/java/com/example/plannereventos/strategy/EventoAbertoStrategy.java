package com.example.plannereventos.strategy;

import com.example.plannereventos.exception.EventoSemVagasException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.InscricaoRepository;
import org.springframework.stereotype.Component;

@Component
public class EventoAbertoStrategy implements ElegibilidadeStrategy {

    private final InscricaoRepository inscricaoRepository;

    public EventoAbertoStrategy(InscricaoRepository inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    @Override
    public void validarElegibilidade(Evento evento, Participante participante) {
        int inscricoesConfirmadas = inscricaoRepository.contarConfirmadasPorEvento(evento.getId());

        if (inscricoesConfirmadas >= evento.getCapacidadeMaxima()) {
            throw new EventoSemVagasException(evento.getId());
        }
    }

    @Override
    public ModalidadeEvento getModalidade() {
        return ModalidadeEvento.ABERTO;
    }
}