package com.example.plannereventos.notificacao;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificadorSMS implements Notificador {

    private static final Logger log = LoggerFactory.getLogger(NotificadorSMS.class);

    private final boolean simularFalha;

    public NotificadorSMS(@Value("${notificacao.sms.simular-falha:false}") boolean simularFalha) {
        this.simularFalha = simularFalha;
    }

    @Override
    public void notificar(Participante participante, Evento evento, Inscricao inscricao) {
        if (simularFalha) {
            throw new IllegalStateException("Servico de SMS indisponivel (falha simulada)");
        }

        log.info("[SMS] Para: {} | Inscricao {} em '{}': {}.",
                participante.getNome(),
                inscricao.getId(),
                evento.getTitulo(),
                inscricao.getStatus());
    }

    @Override
    public String getCanal() {
        return "SMS";
    }
}

