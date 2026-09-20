package com.example.plannereventos.repository;

import com.example.plannereventos.model.Inscricao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InscricaoRepository {

    private final Map<Integer, Inscricao> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public Inscricao salvar(Inscricao inscricao) {
        if (inscricao.getId() <= 0) {
            inscricao.setId(idSequence.getAndIncrement());
        }
        storage.put(inscricao.getId(), inscricao);
        return inscricao;
    }

    public List<Inscricao> listarTodos() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Inscricao> buscarPorId(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Inscricao> listarPorEvento(int idEvento) {
        return storage.values().stream()
                .filter(i -> i.getIdEvento() == idEvento)
                .toList();
    }

    public List<Inscricao> listarPorParticipante(UUID participanteId) {
        return storage.values().stream()
                .filter(i -> participanteId.equals(i.getParticipanteId()))
                .toList();
    }

    public int contarConfirmadasPorEvento(int idEvento) {
        return (int) storage.values().stream()
                .filter(i -> i.getIdEvento() == idEvento && "CONFIRMADA".equalsIgnoreCase(i.getStatus()))
                .count();
    }

    public Optional<Inscricao> buscarPorEventoEParticipante(int eventoId, UUID participanteId) {
        if (participanteId == null) return Optional.empty();
        return storage.values().stream()
                .filter(i -> i.getIdEvento() == eventoId && participanteId.equals(i.getParticipanteId()))
                .findFirst();
    }

    public boolean existeConfirmada(int eventoId, UUID participanteId) {
        if (participanteId == null) return false;
        return storage.values().stream()
                .anyMatch(i -> i.getIdEvento() == eventoId
                        && participanteId.equals(i.getParticipanteId())
                        && "CONFIRMADA".equalsIgnoreCase(i.getStatus()));
    }

    public void cancelarTodasPorEvento(int eventoId) {
        storage.values().stream()
                .filter(i -> i.getIdEvento() == eventoId && "CONFIRMADA".equalsIgnoreCase(i.getStatus()))
                .forEach(i -> i.setStatus("CANCELADA"));
    }
}