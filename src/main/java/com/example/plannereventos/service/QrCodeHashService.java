package com.example.plannereventos.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class QrCodeHashService {

    private static final String ALGORITMO = "SHA-256";
    private static final String SEPARADOR = "|";

    public String gerarHash(int eventoId, UUID participanteId, LocalDateTime dataInscricao) {
        String conteudo = eventoId + SEPARADOR + participanteId + SEPARADOR + dataInscricao;

        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
            byte[] hash = digest.digest(conteudo.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponivel: " + ALGORITMO, e);
        }
    }
}