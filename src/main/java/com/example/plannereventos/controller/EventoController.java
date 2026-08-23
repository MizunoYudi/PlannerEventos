package controller;

import dto.EventoCreateRequest;
import dto.EventoResponse;
import dto.InscricaoCreateRequest;
import model.Inscricao;
import service.EventoService;
import service.InscricaoService;

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

    // POST /api/eventos/{eventoId}/inscricoes
    @PostMapping("/{eventoId}/inscricoes")
    public ResponseEntity<?> realizarInscricao(
            @PathVariable Long eventoId,
            @RequestBody InscricaoCreateRequest request) {
        @RequestBody InscricaoCreateRequest request){
            try {
                Inscricao inscricaoCriada = inscricaoService.inscrever(eventoId, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(inscricaoCriada); // 201
            } catch (RuntimeException e) {
                if (e.getMessage().contains("não encontrado")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
            }
        }
    }