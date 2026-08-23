package com.example.plannereventos.repository;

import com.example.plannereventos.model.Evento;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepository {

    private List<Evento> eventos = new ArrayList<>();

    //criar um evento
    public Evento salvar(Evento evento){
        evento.setId(eventos.size() + 1);
        eventos.add(evento);
        return evento;
    }
    //listar todos os eventos
    public List<Evento> listar(){
        return eventos;
    }
    //buscando um evento pelo id correspondente
    public Evento buscar(int id) {
        for(Evento evento : eventos){
            if(evento.getId() ==  id){
                return evento;
            };
        }
        return null;
    }
    //atualizando um evento através do id da lista e comparando se é igual ao id do evento passado por parametro
    public Evento atualizar(Evento evento){
        for (int i = 0; i < eventos.size(); i++){
           if(eventos.get(i).getId() == evento.getId()){
               eventos.set(i, evento);
               return evento;
           }
        }
        return null;
    }

    //cancelando um evento.... buscando pelo id e atualizando o 'status' do evento
    public Evento cancelar(int id) {
        Evento evento = buscar(id);
        if(evento != null){
            evento.setStatus("CANCELADO");
            atualizar(evento);

            return evento;
        }
        else{
            return null;
        }
    }
}
