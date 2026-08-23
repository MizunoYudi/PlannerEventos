package service;

import dto.InscricaoResponse;
import dto.InscricaoCreateRequest;
import model.Participante;
import repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import model.Evento;
import model.Inscricao;
import repository.EventoRepository;
import repository.ParticipanteRepository;

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

    public Inscricao inscrever(Long eventoId, InscricaoCreateRequest request) {
        Evento evento = eventoRepository.buscarPorId(eventoId);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado.");
        }

        Long participanteId = request.getParticipanteId();
        if (participanteRepository.buscarPorId(participanteId) == null) {
            throw new RuntimeException("Participante não encontrado.");
        }

        if ("CANCELADO".equalsIgnoreCase(evento.getStatus())) {
            throw new RuntimeException("Não é permitido realizar inscrições em eventos cancelados.");
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHorarioInicio());
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
}
