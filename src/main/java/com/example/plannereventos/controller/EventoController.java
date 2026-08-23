package com.example.plannereventos.controller;


import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.model.Evento;
import com.example.plannereventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoCreateRequest request) {
        EventoResponse response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
