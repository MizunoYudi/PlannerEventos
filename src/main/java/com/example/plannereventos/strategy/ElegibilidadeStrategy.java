package com.example.plannereventos.strategy;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;

public interface ElegibilidadeStrategy {

    void validarElegibilidade(Evento evento, Participante participante);

    ModalidadeEvento getModalidade();
}
