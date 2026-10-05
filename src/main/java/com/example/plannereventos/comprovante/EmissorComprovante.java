package com.example.plannereventos.comprovante;

import com.example.plannereventos.model.Comprovante;
import com.example.plannereventos.model.Inscricao;

public interface EmissorComprovante {

    Comprovante emitir(Inscricao inscricao);
}
