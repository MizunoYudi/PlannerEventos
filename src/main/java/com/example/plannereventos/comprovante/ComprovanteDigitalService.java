package com.example.plannereventos.comprovante;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.example.plannereventos.model.Comprovante;
import com.example.plannereventos.model.Inscricao;

@Service
public class ComprovanteDigitalService
        implements EmissorComprovante, GeradorQRCode {

    @Override
    public Comprovante emitir(Inscricao inscricao) {

        String hash = gerarHash(inscricao);

        String resumo =
                "Comprovante digital - Evento: "
                        + inscricao.getIdEvento()
                        + " - Participante: "
                        + inscricao.getParticipanteId();

        return new Comprovante(
                "DIGITAL",
                resumo,
                hash
        );
    }

    @Override
    public String gerarHash(Inscricao inscricao) {

        String payload =
                inscricao.getIdEvento()
                        + ":"
                        + inscricao.getParticipanteId()
                        + ":"
                        + inscricao.getDataCriacao();

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {
                hash.append(
                        String.format("%02x", b)
                );
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "Nao foi possivel gerar o hash",
                    e
            );
        }
    }
}