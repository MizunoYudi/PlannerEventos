package com.example.plannereventos.service;

import com.example.plannereventos.dto.InscricaoCancelarRequest;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.*;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
public class InscricaoService {

    private static final String STATUS_CONFIRMADA = "CONFIRMADA";
    private static final String STATUS_CANCELADA = "CANCELADA";

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository,
                            EventoRepository eventoRepository,
                            ParticipanteRepository participanteRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public List<InscricaoResponse> listarPorEvento(int eventoId) {
        validarExistenciaEvento(eventoId);
        return inscricaoRepository.listarPorEvento(eventoId)
                .stream()
                .map(InscricaoResponse::new)
                .toList();
    }

    public List<InscricaoResponse> listarPorParticipante(UUID participanteId) {
        validarExistenciaParticipante(participanteId);
        return inscricaoRepository.listarPorParticipante(participanteId)
                .stream()
                .map(InscricaoResponse::new)
                .toList();
    }

    public InscricaoResponse buscarPorEventoEParticipante(int eventoId, UUID participanteId) {
        validarExistenciaEvento(eventoId);
        validarExistenciaParticipante(participanteId);

        Inscricao inscricao = inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        return new InscricaoResponse(inscricao);
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

        validarElegibilidadeModalidade(evento, participante);

        if (inscricaoRepository.existeConfirmada(eventoId, participanteId)) {
            throw new InscricaoDuplicadaException(eventoId, participanteId);
        }

        int inscricoesConfirmadas = inscricaoRepository.contarConfirmadasPorEvento(eventoId);
        if (inscricoesConfirmadas >= evento.getCapacidadeMaxima()) {
            throw new EventoSemVagasException(eventoId);
        }

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setIdEvento(eventoId);
        novaInscricao.setParticipanteId(participanteId);
        novaInscricao.setDataCriacao(LocalDateTime.now());
        novaInscricao.setStatus(STATUS_CONFIRMADA);

        Inscricao inscricaoSalva = inscricaoRepository.salvar(novaInscricao);
        return new InscricaoResponse(inscricaoSalva);
    }

    public void cancelarInscricao(int eventoId, UUID participanteId, InscricaoCancelarRequest request) {
        validarExistenciaEvento(eventoId);
        validarExistenciaParticipante(participanteId);

        Inscricao inscricao = inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        if (!STATUS_CONFIRMADA.equalsIgnoreCase(inscricao.getStatus())) {
            throw new CancelamentoInscricaoInvalidoException();
        }

        inscricao.setStatus(STATUS_CANCELADA);
        inscricao.setMotivoCancelamento(request != null ? request.getMotivoCancelamento() : null);
        inscricaoRepository.salvar(inscricao);
    }

    private void validarElegibilidadeModalidade(Evento evento, Participante participante) {
        if (evento.getModalidade() == ModalidadeEvento.EXCLUSIVO_ALUNOS) {
            if (participante.getMatricula() == null || participante.getMatricula().isBlank()
                    || !Boolean.TRUE.equals(participante.getMatriculaAtiva())) {
                throw new ParticipanteSemMatriculaAtivaException();
            }
        } else if (evento.getModalidade() == ModalidadeEvento.RESTRICAO_IDADE) {
            if (evento.getIdadeMinima() != null && evento.getIdadeMinima() > 0) {
                if (participante.getDataNascimento() == null) {
                    throw new DataNascimentoObrigatoriaException();
                }
                int idadeNoEvento = Period.between(participante.getDataNascimento(), evento.getData()).getYears();
                if (idadeNoEvento < evento.getIdadeMinima()) {
                    throw new IdadeInsuficienteException(evento.getIdadeMinima());
                }
            }
        }
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