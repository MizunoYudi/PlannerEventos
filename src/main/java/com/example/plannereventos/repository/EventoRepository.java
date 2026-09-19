package com.example.plannereventos.repository;

import com.example.plannereventos.model.Evento;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class EventoRepository {

    private final Map<Integer, Evento> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public Evento salvar(Evento evento) {
        if (evento.getId() <= 0) {
            evento.setId(idSequence.getAndIncrement());
        }
        storage.put(evento.getId(), evento);
        return evento;
    }

    public List<Evento> listar() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Evento> buscarPorId(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean existePorId(int id) {
        return storage.containsKey(id);
    }
}