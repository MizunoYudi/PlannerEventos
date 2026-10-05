package com.example.plannereventos.comprovante;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.plannereventos.model.ModalidadeEvento;

class EmissorComprovanteFactoryTest {

    private EmissorComprovanteFactory factory;

    @BeforeEach
    void setUp() {

        factory = new EmissorComprovanteFactory(
                new ComprovanteSimplesService(),
                new ComprovanteDigitalService()
        );
    }

    @Test
    @DisplayName("Evento aberto deve utilizar comprovante simples")
    void abertoDeveUsarComprovanteSimples() {

        EmissorComprovante emissor
                = factory.obter(
                        ModalidadeEvento.ABERTO
                );

        assertInstanceOf(
                ComprovanteSimplesService.class,
                emissor
        );
    }

    @Test
    @DisplayName("Evento exclusivo deve utilizar comprovante digital")
    void exclusivoDeveUsarComprovanteDigital() {

        EmissorComprovante emissor
                = factory.obter(
                        ModalidadeEvento.EXCLUSIVO_ALUNOS
                );

        assertInstanceOf(
                ComprovanteDigitalService.class,
                emissor
        );
    }

    @Test
    @DisplayName("Evento com restricao de idade deve utilizar comprovante digital")
    void restricaoIdadeDeveUsarComprovanteDigital() {

        EmissorComprovante emissor
                = factory.obter(
                        ModalidadeEvento.RESTRICAO_IDADE
                );

        assertInstanceOf(
                ComprovanteDigitalService.class,
                emissor
        );
    }
}
