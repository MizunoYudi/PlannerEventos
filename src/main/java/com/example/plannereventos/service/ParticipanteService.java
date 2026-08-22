package com.example.plannereventos.service;

import com.example.plannereventos.dto.ParticipanteCreateRequest;
import com.example.plannereventos.dto.ParticipanteResponse;
import com.example.plannereventos.exception.EmailJaCadastradoException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.ParticipanteRepository;

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

    public ParticipanteResponse atualizar(UUID id, ParticipanteCreateRequest request) {
        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(id));

        String novoEmail = request.getEmail().trim().toLowerCase();

        if (!participante.getEmail().equalsIgnoreCase(novoEmail) && participanteRepository.existsByEmail(novoEmail)) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        participante.setNomeCompleto(request.getNomeCompleto().trim());
        participante.setEmail(novoEmail);

        Participante atualizado = participanteRepository.save(participante);
        return ParticipanteResponse.fromEntity(atualizado);
    }
}
