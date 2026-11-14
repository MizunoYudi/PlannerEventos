package com.example.plannereventos.repository;

import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.StatusInscricao;
import org.springframework.stereotype.Repository;
import java.util.*;
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
                .filter(i -> i.getIdEvento() == idEvento && i.getStatus() == StatusInscricao.CONFIRMADA)
                .count();
    }

    public Optional<Inscricao> buscarPorEventoEParticipante(int eventoId, UUID participanteId) {
        if (participanteId == null) return Optional.empty();

        List<Inscricao> doParticipante = storage.values().stream()
                .filter(i -> i.getIdEvento() == eventoId && participanteId.equals(i.getParticipanteId()))
                .toList();

        return doParticipante.stream()
                .filter(i -> i.getStatus() == StatusInscricao.CONFIRMADA)
                .findFirst()
                .or(() -> doParticipante.stream().max(Comparator.comparingInt(Inscricao::getId)));
    }

    public boolean existeAtivaOuConfirmada(int eventoId, UUID participanteId) {
        if (participanteId == null) return false;
        return storage.values().stream()
                .anyMatch(i -> i.getIdEvento() == eventoId
                        && participanteId.equals(i.getParticipanteId())
                        && (i.getStatus() == StatusInscricao.PENDENTE || i.getStatus() == StatusInscricao.CONFIRMADA));
    }

    public void cancelarTodasPorEvento(int eventoId, String motivo) {
        storage.values().stream()
                .filter(i -> i.getIdEvento() == eventoId && i.getStatus() != StatusInscricao.CANCELADA)
                .forEach(i -> {
                    i.setStatus(StatusInscricao.CANCELADA);
                    i.setMotivoCancelamento(motivo);
                });
    }
}