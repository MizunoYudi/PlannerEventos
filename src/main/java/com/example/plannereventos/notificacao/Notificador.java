package com.example.plannereventos.notificacao;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;

public interface Notificador {

    void notificar(Participante participante, Evento evento, Inscricao inscricao);

    void notificarPrazoConfirmacao(Participante participante, Evento evento, Inscricao inscricao, String tempoRestante);

    void notificarVagasEsgotadas(Participante participante, Evento evento, Inscricao inscricao);

    String getCanal();
}
