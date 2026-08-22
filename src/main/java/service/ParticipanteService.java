package service;

import dto.ParticipanteResponse;
import dto.ParticipanteCreateRequest;
import exception.EmailJaCadastradoException;
import model.Participante;
import repository.ParticipanteRepository;

import  org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParticipanteService {
    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public ParticipanteResponse cadastrar(ParticipanteCreateRequest request){
        if(participanteRepository.existsByEmail(request.getEmail().trim())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }
        Participante participante = new Participante();
        participante.setId(UUID.randomUUID());
        participante.setNomeCompleto(request.getNomeCompleto().trim());
        participante.setEmail(request.getEmail().trim().toLowerCase());
        participante.setCriadoEm(LocalDateTime.now());

        Participante salvo = participanteRepository.save(participante);
        return ParticipanteResponse.fromEntity(salvo);
    }
}
