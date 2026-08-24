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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class EventoService {

    private EventoRepository eventoRepository;
    private InscricaoRepository inscricaoRepository;

    public EventoService(EventoRepository eventoRepository, InscricaoRepository inscricaoRepository) {
        this.eventoRepository = eventoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    private void validarEvento(Evento evento){
        if (evento.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O titulo não pode ficar em branco");
        }
        if(evento.getDescricao().isBlank()){
            throw new IllegalArgumentException("A descrição não pode ficar em branco");
        }
        LocalDate dataAtual = LocalDate.now();
        if(evento.getData().isBefore(dataAtual)){
            throw new IllegalArgumentException("O evento não pode ser cadastrado com a data anterior a de hoje.");
        }
        if (!evento.getHoraFim().isAfter(evento.getHoraInicio())) {
            throw new IllegalArgumentException(" O horário de término deve ser posterior ao horário de início. ");
        }
        if(evento.getCapacidadeMaxima() <= 0 ){
            throw new IllegalArgumentException("A capacidade do evento deve ser maior que 0");
        }
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
        evento.setStatus("ATIVO");
        evento.setCriadoEm(LocalDateTime.now());

        eventoRepository.salvar(evento);

        return EventoResponse.fromEntity(evento);
    }

   public EventoResponse atualizar(int id, EventoUpdateRequest request) {
       Evento existente = eventoRepository.buscarPorId(id);
       if (existente == null) {
           throw new IllegalArgumentException("evento não encontrado");
       }

       existente.setTitulo(request.getTitulo());
       existente.setDescricao(request.getDescricao());
       existente.setData(request.getData());
       existente.setHoraInicio(request.getHoraInicio());
       existente.setHoraFim(request.getHoraFim());
       existente.setLocal(request.getLocal());
       existente.setCapacidadeMaxima(request.getCapacidadeMaxima());

       Evento atualizado = eventoRepository.atualizar(existente);
       return new EventoResponse(atualizado);
   }
    public EventoResponse cancelar(int id) {
        if (eventoRepository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("evento não encontrado");
        }
        Evento cancelado = eventoRepository.cancelar(id);
        return new EventoResponse(cancelado);
    }

    public List<EventoResponse> listar(){
        return eventoRepository.listar()
                .stream()
                .map(EventoResponse::new)
                .toList();
    }

    public EventoVagasResponse consultarVagas(int id) {
        Evento evento = eventoRepository.buscarPorId(id);
        if (evento == null) {
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
        if (evento == null) {
            throw new EventoNaoEncontradoException(id);
        }
        return EventoResponse.fromEntity(evento);
    }
}
