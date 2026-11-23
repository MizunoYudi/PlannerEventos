package com.example.plannereventos.service;

import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.notificacao.Notificador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Service
public class NotificacaoService {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final List<Notificador> notificadores;

    public NotificacaoService(List<Notificador> notificadores) {
        this.notificadores = List.copyOf(notificadores);
    }

    public void notificarMudancaDeStatus(Participante participante, Evento evento, Inscricao inscricao) {
        dispararParaTodos("mudanca de status (" + inscricao.getStatus() + ")", inscricao,
                notificador -> notificador.notificar(participante, evento, inscricao));
    }

    public void notificarPrazoConfirmacao(Participante participante, Evento evento,
                                          Inscricao inscricao, Duration tempoRestante) {
        if (tempoRestante.isNegative() || tempoRestante.isZero()) {
            log.info("Prazo de confirmacao da inscricao {} ja encerrado; lembrete nao enviado.", inscricao.getId());
            return;
        }

        String tempoFormatado = formatarTempoRestante(tempoRestante);
        dispararParaTodos("lembrete de prazo", inscricao,
                notificador -> notificador.notificarPrazoConfirmacao(participante, evento, inscricao, tempoFormatado));
    }

    public void notificarVagasEsgotadas(Participante participante, Evento evento, Inscricao inscricao) {
        dispararParaTodos("aviso de vagas esgotadas", inscricao,
                notificador -> notificador.notificarVagasEsgotadas(participante, evento, inscricao));
    }

    private void dispararParaTodos(String tipo, Inscricao inscricao, Consumer<Notificador> envio) {
        for (Notificador notificador : notificadores) {
            try {
                envio.accept(notificador);
            } catch (RuntimeException ex) {
                log.error("Falha ao enviar {} via {} para a inscricao {}: {}",
                        tipo,
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
        return minutos > 0 ? minutos + "min" : "menos de 1min";
    }
}