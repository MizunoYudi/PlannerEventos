package com.example.plannereventos.notificacao;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificadorEmail implements Notificador {

    private static final Logger log = LoggerFactory.getLogger(NotificadorEmail.class);

    @Override
    public void notificar(Participante participante, Evento evento, Inscricao inscricao) {
        log.info("[E-MAIL] Para: {} | Ola, {}! Sua inscricao {} no evento '{}' esta {}.",
                participante.getEmail(),
                participante.getNome(),
                inscricao.getId(),
                evento.getTitulo(),
                inscricao.getStatus());
    }

    @Override
    public String getCanal() {
        return "E-MAIL";
    }
}

