package com.example.plannereventos.service;

import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.ParticipanteRepository;

@Service
public class InscricaoService {
    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository, EventoRepository eventoRepository,
                            ParticipanteRepository participanteRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public List<Inscricao> listarPorEvento(int eventoId) {
        if (eventoRepository.buscarPorId(eventoId) == null) {
            throw new RuntimeException("Evento não encontrado.");
        }
        return inscricaoRepository.listarPorEvento(eventoId);
    }

    public List<Inscricao> listarPorParticipante(UUID participanteId) {
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }
        return inscricaoRepository.listarPorParticipante(participanteId);
    }

    public Inscricao buscarPorEventoEParticipante(int eventoId, UUID participanteId) {
        if (eventoRepository.buscarPorId(eventoId) == null) {
            throw new RuntimeException("Evento não encontrado.");
        }
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }
        for (Inscricao inscricao : inscricaoRepository.listar()) {
            if (inscricao.getIdEvento() == eventoId && inscricao.getParticipanteId() == participanteId) {
                return inscricao;
            }
        }
        throw new RuntimeException("Inscrição não encontrada para este participante no evento.");
    }

    public Inscricao inscrever(int eventoId, InscricaoCreateRequest request) {
        Evento evento = eventoRepository.buscarPorId(eventoId);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado.");
        }

        UUID participanteId = request.getParticipanteId();
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }

        if ("CANCELADO".equalsIgnoreCase(evento.getStatus())) {
            throw new RuntimeException("Não é permitido realizar inscrições em eventos cancelados.");
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (LocalDateTime.now().isAfter(inicioEvento)) {
            throw new RuntimeException("Não é permitido realizar inscrição após o início do evento.");
        }

        for (Inscricao inscricao : inscricaoRepository.listarPorEvento(eventoId)) {
            if (inscricao.getParticipanteId().equals(participanteId) &&
                    "CONFIRMADA".equalsIgnoreCase(inscricao.getStatus())) {
                throw new RuntimeException("Participante já possui inscrição confirmada neste evento.");
            }
        }

        int inscricoesConfirmadas = inscricaoRepository.contarConfirmadasPorEvento(eventoId);
        if (inscricoesConfirmadas >= evento.getCapacidadeMaxima()) {
            throw new RuntimeException("Evento sem vagas disponíveis.");
        }

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setIdEvento(eventoId);
        novaInscricao.setParticipanteId(participanteId);
        novaInscricao.setDataInscricao(LocalDateTime.now());
        novaInscricao.setStatus("CONFIRMADA");

        return inscricaoRepository.cadastrar(novaInscricao);
    }

    public void cancelarInscricao(int eventoId, UUID participanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado.");
        }
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }
        Inscricao inscricao = inscricaoRepository.buscarPorEventoEParticipante(eventoId, participanteId);
        if (inscricao == null) {
            throw new RuntimeException("Inscrição não encontrada para este participante no evento.");
        }
        if (!"CONFIRMADA".equalsIgnoreCase(inscricao.getStatus())) {
            throw new RuntimeException("Apenas inscrições com status CONFIRMADA podem ser canceladas.");
        }

        inscricao.setStatus("CANCELADA");
    }
}
