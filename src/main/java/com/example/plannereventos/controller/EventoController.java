package com.example.plannereventos.controller;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.service.EventoService;
import com.example.plannereventos.service.InscricaoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final InscricaoService inscricaoService;

    public EventoController(EventoService eventoService, InscricaoService inscricaoService) {
        this.eventoService = eventoService;
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoCreateRequest request) {
        EventoResponse response = eventoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(@PathVariable int id, @Valid @RequestBody EventoUpdateRequest request) {
        EventoResponse response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/cancelamento")
    public Evento cancelar(@PathVariable int id){
        return service.cancelar(id);
    }
}