package com.example.plannereventos.service;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.strategy.EmissorComprovanteStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class ComprovanteService {

    private final Map<ModalidadeEvento, EmissorComprovanteStrategy> emissores = new EnumMap<>(ModalidadeEvento.class);

    public ComprovanteService(List<EmissorComprovanteStrategy> listaEmissores) {
        for (EmissorComprovanteStrategy emissor : listaEmissores) {
            for (ModalidadeEvento modalidade : emissor.getModalidades()) {
                if (emissores.putIfAbsent(modalidade, emissor) != null) {
                    throw new IllegalStateException(
                            "Mais de um emissor de comprovante para a modalidade: " + modalidade);
                }
            }
        }
    }

    public ComprovanteResponse emitir(Evento evento, Participante participante, Inscricao inscricao) {
        EmissorComprovanteStrategy emissor = emissores.get(evento.getModalidade());

        if (emissor == null) {
            throw new IllegalStateException(
                    "Emissor de comprovante nao encontrado para: " + evento.getModalidade());
        }

        return emissor.emitir(evento, participante, inscricao);
    }
}