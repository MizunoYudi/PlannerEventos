package service;

import dto.InscricaoResponse;
import dto.InscricaoCreateRequest;
import model.Participante;
import repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

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

    public List<Inscricao> listarPorEvento(Long eventoId) {
        if (eventoRepository.buscarPorId(eventoId) == null) {
            throw new RuntimeException("Evento não encontrado.");
        }
        return inscricaoRepository.listarPorEvento(eventoId);
    }

    public List<Inscricao> listarPorParticipante(Long participanteId) {
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }
        return inscricaoRepository.listarPorParticipante(participanteId);
    }

    public Inscricao buscarPorEventoEParticipante(Long eventoId, Long participanteId) {
        if (eventoRepository.buscarPorId(eventoId) == null) {
            throw new RuntimeException("Evento não encontrado.");
        }
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }
        for (Inscricao inscricao : inscricaoRepository.listar()) {
            if (inscricao.getIdEvento().equals(eventoId) && inscricao.getParticipanteId().equals(participanteId)) {
                return inscricao;
            }
        }
        throw new RuntimeException("Inscrição não encontrada para este participante no evento.");
    }
}
