package com.example.plannereventos.scheduler;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.service.EventoService;
import com.example.plannereventos.service.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class LembretePrazoConfirmacaoScheduler {

    private static final Logger log = LoggerFactory.getLogger(LembretePrazoConfirmacaoScheduler.class);

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final NotificacaoService notificacaoService;
    private final EventoService eventoService;
    @Value("${notificacao.prazo.intervalo-lembrete-minutos:15}")
    private long intervaloLembreteMinutos = 15;

    public LembretePrazoConfirmacaoScheduler(InscricaoRepository inscricaoRepository,
                                             EventoRepository eventoRepository,
                                             ParticipanteRepository participanteRepository,
                                             NotificacaoService notificacaoService, EventoService eventoService) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.notificacaoService = notificacaoService;
        this.eventoService = eventoService;
    }

    @Scheduled(fixedRateString = "${notificacao.prazo.intervalo-ms:90000}")
    public void notificarInscricoesPendentes() {
        for (Inscricao inscricao : inscricaoRepository.listarPendentes()) {
            try {
                this.processar(inscricao);
            } catch (RuntimeException ex) {
                log.error("Erro ao processar lembrete de prazo da inscricao {}: {}",
                        inscricao.getId(),
                        ex.getMessage(),
                        ex);
            }
        }
    }

    private static final String MOTIVO_EXPIRACAO = "Prazo de confirmacao expirado";

    private void processar(Inscricao inscricao) {
        Evento evento = eventoRepository.buscarPorId(inscricao.getIdEvento()).orElse(null);
        if (evento == null || "CANCELADO".equalsIgnoreCase(evento.getStatus())) {
            return;
        }

        Duration restante = eventoService.obterTempoRestante(evento);
        if (restante.isNegative() || restante.isZero()) {
            expirar(inscricao, evento);
            return;
        }

        LocalDateTime agora = LocalDateTime.now(Fuso.BRASILIA);
        if (!inscricao.deveReceberLembrete(agora, Duration.ofMinutes(intervaloLembreteMinutos))) {
            return;
        }

        Participante participante = participanteRepository.buscarPorId(inscricao.getParticipanteId()).orElse(null);
        if (participante == null) {
            log.warn("Participante {} nao encontrado para a inscricao {}",
                    inscricao.getParticipanteId(), inscricao.getId());
            return;
        }

        notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricao, restante);
        inscricao.registrarLembrete(agora);
        inscricaoRepository.salvar(inscricao);
    }

    private void expirar(Inscricao inscricao, Evento evento) {
        inscricao.cancelar(MOTIVO_EXPIRACAO);
        inscricaoRepository.salvar(inscricao);
        log.info("Inscricao {} cancelada: prazo de confirmacao expirado.", inscricao.getId());

        participanteRepository.buscarPorId(inscricao.getParticipanteId())
                .ifPresent(participante ->
                        notificacaoService.notificarMudancaDeStatus(participante, evento, inscricao));
    }
}
