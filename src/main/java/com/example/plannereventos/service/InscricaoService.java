package com.example.plannereventos.service;

import com.example.plannereventos.dto.ComprovanteResponse;
import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.*;
import com.example.plannereventos.model.*;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import com.example.plannereventos.strategy.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final Map<ModalidadeEvento, ElegibilidadeStrategy> estrategiasElegibilidade;
    private final Map<ModalidadeEvento, PoliticaCancelamentoStrategy> politicasCancelamento;
    private final Map<ModalidadeEvento, CalculoValorFinalStrategy> estrategiasCalculo;
    private final ComprovanteService comprovanteService;
    private final NotificacaoService notificacaoService;
    @Value("${notificacao.prazo.confirmacao-horas:24}")
    private long prazoConfirmacaoHoras = 24;

    public InscricaoService(
            InscricaoRepository inscricaoRepository,
            EventoRepository eventoRepository,
            ParticipanteRepository participanteRepository,
            List<ElegibilidadeStrategy> listaEstrategias,
            List<PoliticaCancelamentoStrategy> listaPoliticasCancelamento,
            List<CalculoValorFinalStrategy> listaEstrategiasCalculo,
            ComprovanteService comprovanteService,
            NotificacaoService notificacaoService) {

        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.estrategiasCalculo = mapearCalculosPorModalidade(listaEstrategiasCalculo);
        this.estrategiasElegibilidade = listaEstrategias.stream()
                .collect(Collectors.toMap(
                        ElegibilidadeStrategy::getModalidade,
                        Function.identity()));

        this.politicasCancelamento = listaPoliticasCancelamento.stream()
                .collect(Collectors.toMap(
                        PoliticaCancelamentoStrategy::getModalidade,
                        Function.identity()));
        this.comprovanteService = comprovanteService;
        this.notificacaoService = notificacaoService;
    }

    public List<InscricaoResponse> listarPorEvento(int eventoId) {
        Evento evento = buscarEventoOuFalhar(eventoId);

        return inscricaoRepository
                .listarPorEvento(eventoId)
                .stream()
                .map(inscricao -> {
                    Participante participante = buscarParticipanteOuFalhar(inscricao.getParticipanteId());

                    InscricaoResponse response = new InscricaoResponse(inscricao);
                    response.setNomeParticipante(participante.getNome());

                    if (inscricao.getStatus() == StatusInscricao.CONFIRMADA) {
                        response.setComprovante(comprovanteService.emitir(evento, participante, inscricao));
                    }

                    return response;
                })
                .toList();
    }

    public List<InscricaoResponse> listarPorParticipante(UUID participanteId) {
        validarExistenciaParticipante(participanteId);
        return inscricaoRepository.listarPorParticipante(participanteId).stream()
                .map(InscricaoResponse::new)
                .toList();
    }

    public InscricaoResponse buscarPorEventoEParticipante(
            int eventoId,
            UUID participanteId) {

        Evento evento = buscarEventoOuFalhar(eventoId);
        Participante participante = buscarParticipanteOuFalhar(participanteId);

        Inscricao inscricao = inscricaoRepository
                .buscarPorEventoEParticipante(
                        eventoId,
                        participanteId
                )
                .orElseThrow(
                        () -> new InscricaoNaoEncontradaException(
                                eventoId,
                                participanteId
                        )
                );
        InscricaoResponse response = new InscricaoResponse(inscricao);
        response.setNomeParticipante(participante.getNome());

        if (inscricao.getStatus() == StatusInscricao.CONFIRMADA) {
            response.setComprovante(comprovanteService.emitir(evento, participante, inscricao));
        }
        return response;
    }

    public InscricaoResponse inscrever(int eventoId, InscricaoCreateRequest request) {
        Evento evento = buscarEventoOuFalhar(eventoId);
        UUID participanteId = request.getParticipanteId();
        Participante participante = buscarParticipanteOuFalhar(participanteId);

        if ("CANCELADO".equalsIgnoreCase(evento.getStatus())) {
            throw new EventoCanceladoException(eventoId);
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (!LocalDateTime.now().isBefore(inicioEvento)) {
            throw new EventoJaIniciadoException(eventoId);
        }

        if (!LocalDateTime.now().isBefore(inicioEvento.minusHours(prazoConfirmacaoHoras))) {
            throw new RegraNegocioException(
                    "Inscricoes encerradas: o prazo de confirmacao (24h antes do evento) ja passou.");
        }

        if (inscricaoRepository.existeAtivaOuConfirmada(eventoId, participanteId)) {
            throw new InscricaoDuplicadaException(eventoId, participanteId);
        }

        ElegibilidadeStrategy strategy = estrategiasElegibilidade.get(evento.getModalidade());
        if (strategy != null) {
            strategy.validarElegibilidade(evento, participante);
        }

        LoteInscricao loteAtivo = evento.getLoteAtivo();
        CalculoValorFinalStrategy estrategiaCalculo = obterEstrategiaCalculo(evento.getModalidade());
        double valorFinal = estrategiaCalculo.calcular(evento.getValorBase(), loteAtivo);

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setIdEvento(eventoId);
        novaInscricao.setParticipanteId(participanteId);
        novaInscricao.setDataCriacao(LocalDateTime.now());
        novaInscricao.setStatus(StatusInscricao.PENDENTE);
        novaInscricao.setLoteAtivo(loteAtivo);
        novaInscricao.setValorFinal(valorFinal);

        Inscricao inscricaoSalva = inscricaoRepository.salvar(novaInscricao);

        InscricaoResponse response = new InscricaoResponse(inscricaoSalva);

        notificacaoService.notificarPrazoConfirmacao(participante, evento, inscricaoSalva);

        response.setNomeParticipante(participante.getNome());
        return response;
    }

    public InscricaoResponse confirmarInscricao(int eventoId, UUID participanteId) {
        Evento evento = buscarEventoOuFalhar(eventoId);
        Participante participante = buscarParticipanteOuFalhar(participanteId);

        Inscricao inscricao = inscricaoRepository
                .buscarPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        inscricao.validarConfirmacaoPermitida();
        LocalDateTime limiteConfirmacao = LocalDateTime.of(evento.getData(), evento.getHoraInicio()).minusHours(prazoConfirmacaoHoras);
        if (!LocalDateTime.now().isBefore(limiteConfirmacao)) {
            throw new RegraNegocioException(
                    "O prazo para confirmar a vaga terminou (ate 24h antes do inicio do evento).");
        }

        int confirmadas = inscricaoRepository.contarConfirmadasPorEvento(eventoId);
        if (confirmadas >= evento.getCapacidadeMaxima()) {
            throw new EventoSemVagasException(eventoId);
        }

        inscricao.confirmar();
        Inscricao inscricaoSalva = inscricaoRepository.salvar(inscricao);

        ComprovanteResponse comprovante = comprovanteService.emitir(evento, participante, inscricaoSalva);

        notificacaoService.notificarMudancaDeStatus(participante, evento, inscricaoSalva);
        this.notificarVagasPreenchidas(evento);

        InscricaoResponse response = new InscricaoResponse(inscricaoSalva, comprovante);
        response.setNomeParticipante(participante.getNome());

        return response;
    }

    private void notificarVagasPreenchidas(Evento evento) {
        if(this.inscricaoRepository.contarConfirmadasPorEvento(evento.getId()) >= evento.getCapacidadeMaxima()) {
            List<Inscricao> inscricoesPendentes = this.inscricaoRepository.listarPendentes().stream()
                    .filter(inscricao -> inscricao.getIdEvento() == evento.getId())
                    .toList();
            for (Inscricao inscricao : inscricoesPendentes) {
                Participante participante = buscarParticipanteOuFalhar(inscricao.getParticipanteId());
                this.notificacaoService.notificarVagasEsgotadas(participante, evento, inscricao);
                this.cancelarInscricao(evento.getId(), inscricao.getParticipanteId(), new InscricaoCancelarRequest());
            }
        }
    }

    public void cancelarInscricao(int eventoId, UUID participanteId, InscricaoCancelarRequest request) {
        Evento evento = buscarEventoOuFalhar(eventoId);

        Participante participante = buscarParticipanteOuFalhar(participanteId);

        Inscricao inscricao = inscricaoRepository
                .buscarPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        inscricao.validarCancelamentoPermitido();

        String motivo = request != null ? request.getMotivoCancelamento() : null;

        PoliticaCancelamentoStrategy politica = politicasCancelamento.get(evento.getModalidade());
        if (politica == null) {
            throw new IllegalStateException("Politica de cancelamento nao encontrada para: " + evento.getModalidade());
        }

        politica.validarCancelamento(inscricao, motivo);

        inscricao.cancelar(motivo);
        inscricaoRepository.salvar(inscricao);
        notificacaoService.notificarMudancaDeStatus(participante, evento, inscricao);
    }

    private CalculoValorFinalStrategy obterEstrategiaCalculo(ModalidadeEvento modalidade) {
        CalculoValorFinalStrategy estrategia = estrategiasCalculo.get(modalidade);
        if (estrategia == null) {
            throw new IllegalStateException("Estrategia de calculo nao encontrada para: " + modalidade);
        }
        return estrategia;
    }

    private static Map<ModalidadeEvento, CalculoValorFinalStrategy> mapearCalculosPorModalidade(
            List<CalculoValorFinalStrategy> estrategias) {
        Map<ModalidadeEvento, CalculoValorFinalStrategy> mapa = new EnumMap<>(ModalidadeEvento.class);
        for (CalculoValorFinalStrategy estrategia : estrategias) {
            for (ModalidadeEvento modalidade : estrategia.getModalidades()) {
                if (mapa.putIfAbsent(modalidade, estrategia) != null) {
                    throw new IllegalStateException(
                            "Mais de uma estrategia de calculo para a modalidade: " + modalidade);
                }
            }
        }
        return mapa;
    }

    private Evento buscarEventoOuFalhar(int eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventoId));
    }

    private Participante buscarParticipanteOuFalhar(UUID participanteId) {
        return participanteRepository.buscarPorId(participanteId)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(participanteId));
    }

    private void validarExistenciaEvento(int eventoId) {
        if (!eventoRepository.existePorId(eventoId)) {
            throw new EventoNaoEncontradoException(eventoId);
        }
    }

    private void validarExistenciaParticipante(UUID participanteId) {
        if (!participanteRepository.existePorId(participanteId)) {
            throw new ParticipanteNaoEncontradoException(participanteId);
        }
    }
}