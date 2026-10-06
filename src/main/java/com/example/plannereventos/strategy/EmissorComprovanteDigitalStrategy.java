package com.example.plannereventos.strategy;

import com.example.plannereventos.dto.ComprovanteDigitalResponse;
import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.QrCode;
import com.example.plannereventos.service.QrCodeHashService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EmissorComprovanteDigitalStrategy implements EmissorComprovanteStrategy, GeradorQrCode {

    private final QrCodeHashService qrCodeHashService;

    public EmissorComprovanteDigitalStrategy(QrCodeHashService qrCodeHashService) {
        this.qrCodeHashService = qrCodeHashService;
    }

    @Override
    public ComprovanteResponse emitir(Evento evento, Participante participante, Inscricao inscricao) {
        return new ComprovanteDigitalResponse(
                inscricao.getId(),
                evento.getId(),
                participante.getId(),
                inscricao.getDataCriacao(),
                participante.getNome(),
                evento.getTitulo(),
                evento.getData(),
                evento.getHoraInicio(),
                evento.getHoraFim(),
                evento.getLocal(),
                evento.getModalidade(),
                gerarQrCode(evento, participante, inscricao));
    }

    @Override
    public QrCode gerarQrCode(Evento evento, Participante participante, Inscricao inscricao) {
        String hash = qrCodeHashService.gerarHash(
                evento.getId(),
                participante.getId(),
                inscricao.getDataCriacao());

        String payload = "PLANNEREVENTOS"
                + "|evento=" + evento.getId()
                + "|participante=" + participante.getId()
                + "|hash=" + hash;

        return new QrCode(hash, payload);
    }

    @Override
    public Set<ModalidadeEvento> getModalidades() {
        return Set.of(ModalidadeEvento.EXCLUSIVO_ALUNOS, ModalidadeEvento.RESTRICAO_IDADE);
    }
}