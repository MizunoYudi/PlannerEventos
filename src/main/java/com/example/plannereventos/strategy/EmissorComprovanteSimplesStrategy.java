package com.example.plannereventos.strategy;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.dto.ComprovanteSimplesResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class EmissorComprovanteSimplesStrategy implements EmissorComprovanteStrategy {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public ComprovanteResponse emitir(Evento evento, Participante participante, Inscricao inscricao) {
        return new ComprovanteSimplesResponse(
                inscricao.getId(),
                evento.getId(),
                participante.getId(),
                inscricao.getDataCriacao(),
                montarResumo(evento, participante, inscricao));
    }

    @Override
    public Set<ModalidadeEvento> getModalidades() {
        return Set.of(ModalidadeEvento.ABERTO);
    }

    private String montarResumo(Evento evento, Participante participante, Inscricao inscricao) {
        return String.join(System.lineSeparator(),
                "COMPROVANTE DE INSCRIÇÃO: ",
                "Inscricao: " + inscricao.getId() + " (" + inscricao.getStatus() + ")",
                "Participante: " + participante.getNome(),
                "Evento: " + evento.getTitulo(),
                "Data: " + evento.getData().format(FORMATO_DATA)
                        + " as " + evento.getHoraInicio().format(FORMATO_HORA),
                "Local: " + evento.getLocal());
    }
}