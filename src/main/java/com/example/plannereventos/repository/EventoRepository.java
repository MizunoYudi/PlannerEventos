package com.example.plannereventos.repository;

import com.example.plannereventos.model.Evento;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepository {
    private final List<Evento> eventos = new ArrayList<>();

    public Evento salvarEvento(Evento evento) {
        evento.setId(eventos.size() + 1);
        eventos.add(evento);
        return evento;
    }

    public List<Evento> listarEventos() {
        return eventos;
    }

    public Evento atualizarEvento(Evento evento) {
        for(int i = 0; i < eventos.size(); i++){
            if(eventos.get(i).getId() == evento.getId()){
                eventos.set(i, evento);
                return evento;
            }
        }
        return null;
    }

    public Evento buscarPorId(int id) {
        for (Evento evento : eventos) {
            if (evento.getId() == id) {
                return evento;
            }
        }
        return null;
    }

    public Evento cancelarEvento(int id) {
        Evento evento = buscarPorId(id);
        if (evento != null) {
            evento.setStatus("CANCELADO");
            atualizarEvento(evento);
            return evento;
        } else {
            return null;
        }
    }
}
