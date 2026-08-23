package com.example.plannereventos.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorMessage {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> mensagens;
    private String path;

    public ErrorMessage(int status, String error, List<String> mensagens, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensagens = mensagens;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public String getPath() {
        return path;
    }
}