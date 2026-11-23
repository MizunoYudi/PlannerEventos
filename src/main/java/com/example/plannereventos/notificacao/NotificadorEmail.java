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
        String motivo = inscricao.getMotivoCancelamento() != null  ? " Motivo: " + inscricao.getMotivoCancelamento() + "."
                : "";

        log.info("[E-MAIL] Para: {} | Ola, {}! Sua inscricao {} no evento '{}' esta {}.{}",
                participante.getEmail(),
                participante.getNome(),
                inscricao.getId(),
                evento.getTitulo(),
                inscricao.getStatus(),
                motivo);
    }

    @Override
    public void notificarPrazoConfirmacao(Participante participante, Evento evento, Inscricao inscricao, String tempoRestante) {
        log.info("[E-MAIL] Para: {} | Ola, {}! Voce tem {} para CONFIRMAR sua vaga no evento '{}' (inscricao {}).",
                participante.getEmail(),
                participante.getNome(),
                tempoRestante,
                evento.getTitulo(),
                inscricao.getId());
    }

    @Override
    public void notificarVagasEsgotadas(Participante participante, Evento evento, Inscricao inscricao) {
        log.info("[E-MAIL] Para: {} | Ola, {}! As vagas para o evento {} foram esgotadas! Devido a isso, a inscricao {} sera cancelada.",
                participante.getEmail(),
                participante.getNome(),
                evento.getTitulo(),
                inscricao.getId());
    }

    @Override
    public String getCanal() {
        return "E-MAIL";
    }
}

