package com.example.plannereventos.repository;

import com.example.plannereventos.model.Evento;

import java.util.List;

public interface EventoRepository {

    //métodos utlizados para eventos

    //Listar eventos
    List<Evento> listar();

    //Salvar eventos
    Evento salvar(Evento evento);

    //Procurar Evento
    Evento buscar(int id);

    //Atualizar Evento
    Evento atualizar(Evento evento);

    //Cancelar Evento
    Evento cancelar(int id);

}
