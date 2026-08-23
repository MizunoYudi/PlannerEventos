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
        if(evento.getCapacidade() <= 0 ){
            throw new IllegalArgumentException("A capacidade do evento deve ser maior que 0");
        }
    }

    public EventoResponse cadastrar(EventoCreateRequest request) {
        Evento evento = new Evento();
        evento.setTitulo(request.getTitulo());
        evento.setDescricao(request.getDescricao());
        evento.setData(request.getData());
        evento.setHorarioInicio(request.getHorarioInicio());
        evento.setHorarioTermino(request.getHorarioTermino());
        evento.setLocal(request.getLocal());
        evento.setCapacidade(request.getCapacidade());
        evento.setStatus("ATIVO");
        evento.setRegistroCriacao(LocalDateTime.now());

        Evento salvo = eventoRepository.salvar(evento);

        return new EventoResponse(salvo);
    }
   /* public Evento cadastrar(Evento evento){
        evento.setStatus("ATIVO");
        evento.setRegistroCriacao(LocalDateTime.now());
        validarEvento(evento);
        return eventoRepository.salvar(evento);
    }*/
   public EventoResponse atualizar(int id, EventoUpdateRequest request) {
       Evento existente = eventoRepository.buscar(id);
       if (existente == null) {
           throw new IllegalArgumentException("evento não encontrado");
       }

       existente.setTitulo(request.getTitulo());
       existente.setDescricao(request.getDescricao());
       existente.setData(request.getData());
       existente.setHorarioInicio(request.getHorarioInicio());
       existente.setHorarioTermino(request.getHorarioTermino());
       existente.setLocal(request.getLocal());
       existente.setCapacidade(request.getCapacidade());

       Evento atualizado = eventoRepository.atualizar(existente);
       return new EventoResponse(atualizado);
   }
    public Evento cancelar(int id){
        if(eventoRepository.buscar(id) == null){
            throw new IllegalArgumentException("evento não encontrado");
        }
        return eventoRepository.cancelar(id);
    }
    public List<Evento> listar(){
        return eventoRepository.listar();
    }
}
