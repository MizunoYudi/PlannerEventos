package com.example.plannereventos.service;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.notificacao.Notificador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final List<Notificador> notificadores;

    public NotificacaoService(List<Notificador> notificadores) {
        this.notificadores = List.copyOf(notificadores);
    }

    public void notificarMudancaDeStatus(Participante participante, Evento evento, Inscricao inscricao) {
        for (Notificador notificador : notificadores) {
            try {
                notificador.notificar(participante, evento, inscricao);
            } catch (RuntimeException ex) {
                log.error("Falha ao enviar notificacao via {} para a inscricao {} (status {}): {}",
                        notificador.getCanal(),
                        inscricao.getId(),
                        inscricao.getStatus(),
                        ex.getMessage(),
                        ex);
            }
        }
    }
}
