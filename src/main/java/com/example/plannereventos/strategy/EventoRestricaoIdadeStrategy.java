package com.example.plannereventos.strategy;

import com.example.plannereventos.exception.DataNascimentoObrigatoriaException;
import com.example.plannereventos.exception.IdadeNaoPermitidaException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import org.springframework.stereotype.Component;

import java.time.Period;

@Component
public class EventoRestricaoIdadeStrategy implements ElegibilidadeStrategy {

    @Override
    public void validarElegibilidade(Evento evento, Participante participante) {
        if (evento.getIdadeMinima() != null && evento.getIdadeMinima() > 0) {
            if (participante.getDataNascimento() == null) {
                throw new DataNascimentoObrigatoriaException();
            }
            int idadeNoEvento = Period.between(participante.getDataNascimento(), evento.getData()).getYears();
            if (idadeNoEvento < evento.getIdadeMinima()) {
                throw new IdadeNaoPermitidaException(evento.getIdadeMinima());
            }
        }
    }

    @Override
    public ModalidadeEvento getModalidade() {
        return ModalidadeEvento.RESTRICAO_IDADE;
    }
}