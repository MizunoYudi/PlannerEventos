package com.example.plannereventos.comprovante;
import org.springframework.stereotype.Component;

import com.example.plannereventos.model.ModalidadeEvento;

@Component
public class EmissorComprovanteFactory {

    private final ComprovanteSimplesService comprovanteSimplesService;
    private final ComprovanteDigitalService comprovanteDigitalService;

    public EmissorComprovanteFactory(
            ComprovanteSimplesService comprovanteSimplesService,
            ComprovanteDigitalService comprovanteDigitalService) {
        this.comprovanteSimplesService = comprovanteSimplesService;
        this.comprovanteDigitalService = comprovanteDigitalService;
    }

    public EmissorComprovante obter(
            ModalidadeEvento modalidade) {
        if (modalidade == ModalidadeEvento.ABERTO) {
            return comprovanteSimplesService;
        }
        return comprovanteDigitalService;
    }
}
