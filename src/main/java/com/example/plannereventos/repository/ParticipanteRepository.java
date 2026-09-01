package com.example.plannereventos.repository;

import com.example.plannereventos.model.Participante;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ParticipanteRepository {

    private final Map<UUID, Participante> storage = new ConcurrentHashMap<>();

    public Participante salvarParticipante(Participante participante) {
        if (participante.getId() == null) {
            participante.setId(UUID.randomUUID());
        }
        if (participante.getCriadoEm() == null) {
            participante.setCriadoEm(LocalDateTime.now());
        }
        storage.put(participante.getId(), participante);
        return participante;
    }

    public Optional<Participante> buscarPorId(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Participante> buscarPorEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.values().stream()
                .filter(p -> email.equalsIgnoreCase(p.getEmail()))
                .findFirst();
    }

    public List<Participante> listarParticipantes() {
        return new ArrayList<>(storage.values());
    }

    public boolean existePorId(UUID id) {
        return storage.containsKey(id);
    }

    public boolean existePorEmail(String email) {
        if (email == null) return false;
        return storage.values().stream()
                .anyMatch(p -> p.getEmail().equalsIgnoreCase(email.trim()));
    }
}