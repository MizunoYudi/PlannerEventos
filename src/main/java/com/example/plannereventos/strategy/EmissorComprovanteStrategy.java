package com.example.plannereventos.strategy;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;

import java.util.Set;

public interface EmissorComprovanteStrategy {

    ComprovanteResponse emitir(Evento evento, Participante participante, Inscricao inscricao);

    Set<ModalidadeEvento> getModalidades();
}