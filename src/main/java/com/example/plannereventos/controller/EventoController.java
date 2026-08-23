package com.example.plannereventos.controller;


import com.example.plannereventos.model.Evento;
import com.example.plannereventos.service.EventoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service){
        this.service = service;
    }

    @GetMapping
    public List<Evento> listar(){
        return service.listar();
    }
    @PostMapping
    public Evento cadastrar(@RequestBody Evento evento){
        return service.cadastrar(evento);
    }
    @PutMapping("/{id}")
    public Evento atualizar(@PathVariable int id, @RequestBody Evento evento) {
        evento.setId(id);
        return service.atualizar(evento);
    }
    @PatchMapping("/{id}/cancelamento")
    public Evento cancelar(@PathVariable int id){
        return service.cancelar(id);
    }
}
