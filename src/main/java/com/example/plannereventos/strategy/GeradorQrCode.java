package com.example.plannereventos.strategy;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.model.QrCode;

public interface GeradorQrCode {

    QrCode gerarQrCode(Evento evento, Participante participante, Inscricao inscricao);
}