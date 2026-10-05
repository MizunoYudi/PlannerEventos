package com.example.plannereventos.strategy;

import org.springframework.stereotype.Component;

import com.example.plannereventos.exception.MatriculaInvalidaException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;

@Component
public class EventoExclusivoAlunosStrategy implements ElegibilidadeStrategy {

    @Override
    public void validarElegibilidade(Evento evento, Participante participante) {
        if (participante.getMatricula() == null
                || participante.getMatricula().isBlank()
                || !Boolean.TRUE.equals(participante.getMatriculaAtiva())) {
            throw new MatriculaInvalidaException();
        }
    }

    @Override
    public ModalidadeEvento getModalidade() {
        return ModalidadeEvento.EXCLUSIVO_ALUNOS;
    }
}
