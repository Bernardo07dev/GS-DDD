package br.com.fiap.filainvisivel.exception;

import java.time.Instant;

public record ErroResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ErroResponse(int status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path);
    }
}
