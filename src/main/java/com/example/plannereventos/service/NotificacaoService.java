package com.example.plannereventos.service;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.notificacao.Notificador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public void notificarPrazoConfirmacao(Participante participante, Evento evento, Inscricao inscricao) {
        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        LocalDateTime limiteConfirmacao = inicioEvento.minus(Duration.ofHours(24));
        Duration tempoRestante = Duration.between(LocalDateTime.now(), limiteConfirmacao);
        String tempoFormatado = formatarTempoRestante(tempoRestante);
        for (Notificador notificador : notificadores) {
            try {
                notificador.notificarPrazoConfirmacao(participante, evento, inscricao, tempoFormatado);
            } catch (RuntimeException ex) {
                log.error("Falha ao enviar lembrete de prazo via {} para a inscricao {}: {}",
                        notificador.getCanal(),
                        inscricao.getId(),
                        ex.getMessage(),
                        ex);
            }
        }
    }

    private static String formatarTempoRestante(Duration tempo) {
        long horas = tempo.toHours();
        long minutos = tempo.toMinutesPart();
        if (horas > 0) {
            return minutos > 0 ? horas + "h e " + minutos + "min" : horas + "h";
        }
        return Math.max(minutos, 1) + "min";
    }
}
