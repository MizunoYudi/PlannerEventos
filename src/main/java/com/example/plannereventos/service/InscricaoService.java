package com.example.plannereventos.service;

import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.exception.CancelamentoInscricaoInvalidoException;
import com.example.plannereventos.exception.EventoCanceladoException;
import com.example.plannereventos.exception.EventoJaIniciadoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.exception.EventoSemVagasException;
import com.example.plannereventos.exception.InscricaoDuplicadaException;
import com.example.plannereventos.exception.InscricaoNaoEncontradaException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository, EventoRepository eventoRepository, ParticipanteRepository participanteRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public List<InscricaoResponse> listarPorEvento(int eventoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId);

        if (evento == null) {
            throw new EventoNaoEncontradoException(eventoId);
        }
        return inscricaoRepository.listarPorEvento(eventoId).stream().map(InscricaoResponse::new).toList();
    }

    public List<InscricaoResponse> listarPorParticipante(UUID participanteId) {
        participanteRepository.buscarPorId(participanteId).orElseThrow(() -> new ParticipanteNaoEncontradoException(participanteId));
        return inscricaoRepository.listarPorParticipante(participanteId).stream().map(InscricaoResponse::new).toList();
    }

    public InscricaoResponse buscarPorEventoEParticipante(int eventoId, UUID participanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId);
        if (evento == null) {
            throw new EventoNaoEncontradoException(eventoId);
        }
        participanteRepository.buscarPorId(participanteId).orElseThrow(() -> new ParticipanteNaoEncontradoException(participanteId));
        Inscricao inscricao = inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId);
        if (inscricao == null) {
            throw new InscricaoNaoEncontradaException(eventoId, participanteId);
        }
        return new InscricaoResponse(inscricao);
    }

    public InscricaoResponse inscrever(int eventoId, InscricaoCreateRequest request) {
        Evento evento = eventoRepository.buscarPorId(eventoId);
        if (evento == null) {
            throw new EventoNaoEncontradoException(eventoId);
        }

        UUID participanteId = request.getParticipanteId();
        participanteRepository.buscarPorId(participanteId).orElseThrow(() -> new ParticipanteNaoEncontradoException(participanteId));

        if ("CANCELADO".equalsIgnoreCase(evento.getStatus())) {
            throw new EventoCanceladoException(eventoId);
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (!LocalDateTime.now().isBefore(inicioEvento)) {
            throw new EventoJaIniciadoException(eventoId);
        }
        for (Inscricao inscricao : inscricaoRepository.listarPorEvento(eventoId)) {

            boolean mesmoParticipante = inscricao.getParticipanteId().equals(participanteId);
            boolean confirmada = "CONFIRMADA".equalsIgnoreCase(inscricao.getStatus());

            if (mesmoParticipante && confirmada) {
                throw new InscricaoDuplicadaException(eventoId, participanteId);
            }
        }

        int inscricoesConfirmadas = inscricaoRepository.contarConfirmadasPorEvento(eventoId);
        if (inscricoesConfirmadas >= evento.getCapacidadeMaxima()) {
            throw new EventoSemVagasException(eventoId);
        }

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setIdEvento(eventoId);
        novaInscricao.setParticipanteId(participanteId);
        novaInscricao.setCriadoEm(LocalDateTime.now());
        novaInscricao.setStatus("CONFIRMADA");

        Inscricao inscricaoSalva = inscricaoRepository.cadastrar(novaInscricao);

        return new InscricaoResponse(inscricaoSalva);
    }

    public void cancelarInscricao(int eventoId, UUID participanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId);

        if (evento == null) {
            throw new EventoNaoEncontradoException(eventoId);
        }
        participanteRepository.buscarPorId(participanteId).orElseThrow(() -> new ParticipanteNaoEncontradoException(participanteId));
        Inscricao inscricao = inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId);
        if (inscricao == null) {
            throw new InscricaoNaoEncontradaException(eventoId, participanteId);
        }
        if (!"CONFIRMADA".equalsIgnoreCase(inscricao.getStatus())) {
            throw new CancelamentoInscricaoInvalidoException();
        }
        inscricao.setStatus("CANCELADA");
    }
}