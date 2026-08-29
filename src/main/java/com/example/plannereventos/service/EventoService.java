package com.example.plannereventos.service;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.EventoUpdateRequest;
import com.example.plannereventos.dto.EventoVagasResponse;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;

    public EventoService(EventoRepository eventoRepository, InscricaoRepository inscricaoRepository) {
        this.eventoRepository = eventoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public EventoResponse salvarEvento(EventoCreateRequest request) {
        Evento evento = new Evento();
        evento.setTitulo(request.getTitulo());
        evento.setDescricao(request.getDescricao());
        evento.setData(request.getData());
        evento.setHoraInicio(request.getHoraInicio());
        evento.setHoraFim(request.getHoraFim());
        evento.setLocal(request.getLocal());
        evento.setCapacidadeMaxima(request.getCapacidadeMaxima());
        evento.setStatus("ATIVO");
        evento.setCriadoEm(LocalDateTime.now());
        eventoRepository.salvarEvento(evento);
        return EventoResponse.fromEntity(evento);
    }

    public EventoResponse atualizarEvento(int id, EventoUpdateRequest request) {
        Evento existente = eventoRepository.buscarPorId(id);
        if (existeEvento(existente.getId())) {
            throw new EventoNaoEncontradoException(id);
        }
        existente.setTitulo(request.getTitulo());
        existente.setDescricao(request.getDescricao());
        existente.setData(request.getData());
        existente.setHoraInicio(request.getHoraInicio());
        existente.setHoraFim(request.getHoraFim());
        existente.setLocal(request.getLocal());
        existente.setCapacidadeMaxima(request.getCapacidadeMaxima());
        Evento atualizado = eventoRepository.atualizarEvento(existente);
        return new EventoResponse(atualizado);
    }

    private boolean existeEvento(int idEvento){
        return eventoRepository.buscarPorId(idEvento) != null;
    }

    public EventoResponse cancelarEvento(int id) {
        if (existeEvento(id)) {
            throw new EventoNaoEncontradoException(id);
        }
        Evento cancelado = eventoRepository.cancelarEvento(id);
        return new EventoResponse(cancelado);
    }

    public List<EventoResponse> listarEventos() {
        return eventoRepository.listarEventos()
                .stream()
                .map(EventoResponse::new)
                .toList();
    }

    public EventoVagasResponse consultarVagas(int id) {
        Evento evento = eventoRepository.buscarPorId(id);
        if (existeEvento(id)) {
            throw new EventoNaoEncontradoException(id);
        }
        int confirmadas = inscricaoRepository.contarConfirmadasPorEvento(id);
        int vagasDisponiveis = Math.max(0, evento.getCapacidadeMaxima() - confirmadas);
        return new EventoVagasResponse(
                evento.getId(),
                evento.getCapacidadeMaxima(),
                confirmadas,
                vagasDisponiveis
        );
    }

    public EventoResponse buscarPorId(int id) {
        Evento evento = eventoRepository.buscarPorId(id);
        if (existeEvento(id)) {
            throw new EventoNaoEncontradoException(id);
        }
        return EventoResponse.fromEntity(evento);
    }
}
