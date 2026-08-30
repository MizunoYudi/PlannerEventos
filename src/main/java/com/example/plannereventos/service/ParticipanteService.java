package com.example.plannereventos.service;

import com.example.plannereventos.dto.ParticipanteCreateRequest;
import com.example.plannereventos.dto.ParticipanteResponse;
import com.example.plannereventos.exception.EmailJaCadastradoException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.ParticipanteRepository;

import  org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipanteService {
    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public ParticipanteResponse cadastrarParticipante(ParticipanteCreateRequest request){
        if(participanteRepository.existePorEmail(request.getEmail().trim())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }
        Participante participante = new Participante();
        participante.setId(UUID.randomUUID());
        participante.setNome(request.getNome().trim());
        participante.setEmail(request.getEmail().trim().toLowerCase());
        participante.setCriadoEm(LocalDateTime.now());

        Participante salvo = participanteRepository.salvarParticipante(participante);
        return ParticipanteResponse.fromEntity(salvo);
    }

    public ParticipanteResponse atualizarParticipante(UUID id, ParticipanteCreateRequest request) {
        Participante participante = participanteRepository.buscarPorId(id)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(id));

        String novoEmail = request.getEmail().trim().toLowerCase();
        String emailAtual = participante.getEmail().trim().toLowerCase();

        if (!emailAtual.equalsIgnoreCase(novoEmail)) {
            if (participanteRepository.existePorEmail(novoEmail)) {
                throw new EmailJaCadastradoException(request.getEmail());
            }
        }
        participante.setNome(request.getNome().trim());
        participante.setEmail(novoEmail);

        Participante salvo = participanteRepository.salvarParticipante(participante);
        return ParticipanteResponse.fromEntity(salvo);
    }

    public List<ParticipanteResponse> listarParticipantes() {
        return participanteRepository.listarParticipantes()
                .stream()
                .map(ParticipanteResponse::fromEntity)
                .toList();
    }

    public ParticipanteResponse buscarPorId(UUID id) {
        Participante participante = participanteRepository.buscarPorId(id)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(id));
        return ParticipanteResponse.fromEntity(participante);
    }
}
