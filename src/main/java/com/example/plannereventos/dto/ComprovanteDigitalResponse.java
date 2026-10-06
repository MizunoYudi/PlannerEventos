package com.example.plannereventos.dto;

import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.QrCode;
import com.example.plannereventos.model.TipoComprovante;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class ComprovanteDigitalResponse extends ComprovanteResponse {

    private final String nomeParticipante;
    private final String tituloEvento;
    private final LocalDate dataEvento;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime horaFim;

    private final String localEvento;
    private final ModalidadeEvento modalidade;
    private final QrCode qrCode;

    public ComprovanteDigitalResponse(int inscricaoId, int eventoId, UUID participanteId,
                                      LocalDateTime dataInscricao, String nomeParticipante,
                                      String tituloEvento, LocalDate dataEvento,
                                      LocalTime horaInicio, LocalTime horaFim,
                                      String localEvento, ModalidadeEvento modalidade,
                                      QrCode qrCode) {
        super(inscricaoId, eventoId, participanteId, dataInscricao);
        this.nomeParticipante = nomeParticipante;
        this.tituloEvento = tituloEvento;
        this.dataEvento = dataEvento;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.localEvento = localEvento;
        this.modalidade = modalidade;
        this.qrCode = qrCode;
    }

    @Override
    public TipoComprovante getTipo() {
        return TipoComprovante.DIGITAL_COMPLETO;
    }

    public String getNomeParticipante() {
        return nomeParticipante;
    }

    public String getTituloEvento() {
        return tituloEvento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public String getLocalEvento() {
        return localEvento;
    }

    public ModalidadeEvento getModalidade() {
        return modalidade;
    }

    public QrCode getQrCode() {
        return qrCode;
    }
}