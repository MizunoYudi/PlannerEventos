package com.example.plannereventos.service;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.EventoUpdateRequest;
import com.example.plannereventos.dto.EventoVagasResponse;
import com.example.plannereventos.exception.EventoCanceladoException;
import com.example.plannereventos.exception.EventoJaIniciadoException;
import com.example.plannereventos.exception.EventoNaoEncontradoException;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.repository.EventoRepository;
import com.example.plannereventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    private static final String STATUS_ATIVO = "ATIVO";
    private static final String STATUS_CANCELADO = "CANCELADO";

    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;

    public EventoService(EventoRepository eventoRepository, InscricaoRepository inscricaoRepository) {
        this.eventoRepository = eventoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public EventoResponse cadastrar(EventoCreateRequest request) {
        Evento evento = new Evento();
        evento.setTitulo(request.getTitulo());
        evento.setDescricao(request.getDescricao());
        evento.setData(request.getData());
        evento.setHoraInicio(request.getHoraInicio());
        evento.setHoraFim(request.getHoraFim());
        evento.setLocal(request.getLocal());
        evento.setCapacidadeMaxima(request.getCapacidadeMaxima());
        evento.setStatus(STATUS_ATIVO);
        evento.setCriadoEm(LocalDateTime.now());

        eventoRepository.salvar(evento);
        return EventoResponse.fromEntity(evento);
    }

    public EventoResponse atualizar(int id, EventoUpdateRequest request) {
        Evento existente = buscarEventoOuLancarExcecao(id);

        if (STATUS_CANCELADO.equalsIgnoreCase(existente.getStatus())) {
            throw new EventoCanceladoException(id);
        }

        existente.setTitulo(request.getTitulo());
        existente.setDescricao(request.getDescricao());
        existente.setData(request.getData());
        existente.setHoraInicio(request.getHoraInicio());
        existente.setHoraFim(request.getHoraFim());
        existente.setLocal(request.getLocal());
        existente.setCapacidadeMaxima(request.getCapacidadeMaxima());

        eventoRepository.salvar(existente);
        return EventoResponse.fromEntity(existente);
    }

    public EventoResponse cancelar(int id) {
        Evento evento = buscarEventoOuLancarExcecao(id);

        if (STATUS_CANCELADO.equalsIgnoreCase(evento.getStatus())) {
            throw new EventoCanceladoException(id);
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (LocalDateTime.now().isAfter(inicioEvento)) {
            throw new EventoJaIniciadoException(id);
        }

        evento.setStatus(STATUS_CANCELADO);
        eventoRepository.salvar(evento);

        inscricaoRepository.cancelarTodasPorEvento(id);

        return EventoResponse.fromEntity(evento);
    }

    public List<EventoResponse> listar() {
        return eventoRepository.listar()
                .stream()
                .map(EventoResponse::fromEntity)
                .toList();
    }

    public EventoResponse buscarPorId(int id) {
        return EventoResponse.fromEntity(buscarEventoOuLancarExcecao(id));
    }

    public EventoVagasResponse consultarVagas(int id) {
        Evento evento = buscarEventoOuLancarExcecao(id);

        int confirmadas = inscricaoRepository.contarConfirmadasPorEvento(id);
        int vagasDisponiveis = Math.max(0, evento.getCapacidadeMaxima() - confirmadas);

        return new EventoVagasResponse(
                evento.getId(),
                evento.getCapacidadeMaxima(),
                confirmadas,
                vagasDisponiveis
        );
    }

    private Evento buscarEventoOuLancarExcecao(int id) {
        return eventoRepository.buscarPorId(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));
    }
}