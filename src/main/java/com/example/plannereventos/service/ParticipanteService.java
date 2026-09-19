package com.example.plannereventos.service;

import com.example.plannereventos.dto.ParticipanteCreateRequest;
import com.example.plannereventos.dto.ParticipanteResponse;
import com.example.plannereventos.dto.ParticipanteUpdateRequest;
import com.example.plannereventos.exception.EmailJaCadastradoException;
import com.example.plannereventos.exception.ParticipanteNaoEncontradoException;
import com.example.plannereventos.model.Participante;
import com.example.plannereventos.repository.ParticipanteRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipanteService {
    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public ParticipanteResponse cadastrar(ParticipanteCreateRequest request){
        if(participanteRepository.existePorEmail(request.getEmail().trim())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }
        Participante participante = new Participante();
        participante.setId(UUID.randomUUID());
        participante.setNome(request.getNome().trim());
        participante.setEmail(request.getEmail().trim().toLowerCase());
        participante.setCriadoEm(LocalDateTime.now());

        Participante salvo = participanteRepository.salvar(participante);
        return ParticipanteResponse.fromEntity(salvo);
    }

    public ParticipanteResponse atualizar(UUID id, ParticipanteUpdateRequest request) {
        Participante participante = participanteRepository.buscarPorId(id)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(id));

        if (!participante.getEmail().equalsIgnoreCase(request.getEmail().trim())
                && participanteRepository.existePorEmail(request.getEmail().trim())) {
            throw new EmailJaCadastradoException("E-mail já cadastrado");
        }

        participante.setNome(request.getNome().trim());
        participante.setEmail(request.getEmail().trim());

        participanteRepository.salvar(participante);

        return ParticipanteResponse.fromEntity(participante);
    }

    public List<ParticipanteResponse> listar() {
        return participanteRepository.listar()
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
