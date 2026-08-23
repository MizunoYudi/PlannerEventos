package com.example.plannereventos.service;
import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.EventoUpdateRequest;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    private EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
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
        if (!evento.getHorarioTermino().isAfter(evento.getHorarioInicio())) {
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
        evento.setHorarioInicio(LocalDateTime.from(request.getHorarioInicio()));
        evento.setHorarioTermino(LocalDateTime.from(request.getHorarioTermino()));
        evento.setLocal(request.getLocal());
        evento.setCapacidadeMaxima(request.getCapacidadeMaxima());
        evento.setStatus("ATIVO");
        evento.setRegistroCriacao(LocalDateTime.now());

        Evento salvo = eventoRepository.salvar(evento);

        return new EventoResponse(salvo);
    }
   public EventoResponse atualizar(int id, EventoUpdateRequest request) {
       Evento existente = eventoRepository.buscarPorId(id);
       if (existente == null) {
           throw new IllegalArgumentException("evento não encontrado");
       }

       existente.setTitulo(request.getTitulo());
       existente.setDescricao(request.getDescricao());
       existente.setData(request.getData());
       existente.setHorarioInicio(request.getHorarioInicio());
       existente.setHorarioTermino(request.getHorarioTermino());
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

    public List<Evento> listar(){
        return eventoRepository.listar();
    }
}
